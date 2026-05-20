package com.learning.orderservice.service.implementation;



import com.learning.orderservice.dto.*;
import com.learning.orderservice.dto.admin.AdminOrderDTO;
import com.learning.orderservice.dto.admin.AdminOrderFilterRequest;
import com.learning.orderservice.dto.admin.AdminOrderSummaryDTO;
import com.learning.orderservice.dto.types.OrderStatus;
import com.learning.orderservice.entity.*;
import com.learning.orderservice.externalservices.AuthClient;
import com.learning.orderservice.externalservices.PaymentClient;
import com.learning.orderservice.externalservices.ProductClient;
import com.learning.orderservice.repository.OrderRepository;
import com.learning.orderservice.repository.ShipmentRepository;
import com.learning.orderservice.service.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderRepository        orderRepository;
    private final ShipmentRepository     shipmentRepository;
    private final AuthClient userClient;
    private final ProductClient productVariantClient;
    private final PaymentClient paymentClient;

    // ── Paginated list ─────────────────────────────────────────────────────────
    public Page<AdminOrderSummaryDTO> getOrdersPage(AdminOrderFilterRequest req) {

        Pageable pageable = PageRequest.of(req.getPage(), req.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        // 1. DB query (status filter + optional search on razorpayOrderId)
        Page<Orders> page = (req.getStatus() != null)
                ? orderRepository.findByStatus(req.getStatus(), pageable)
                : orderRepository.findAll(pageable);

        List<Orders> orders = page.getContent();
        if (orders.isEmpty()) return page.map(o -> null); // fast path

        // 2. Collect IDs for bulk Feign calls
        List<Long> orderIds = orders.stream().map(Orders::getOrderId).toList();
        List<Long> userIds  = orders.stream().map(Orders::getUserId).distinct().toList();

        // 3. Bulk fetch users and payments in parallel (simple sequential here)
        Map<Long, UserDetailResponseDto>    userMap    = toMap(userClient.getUsersByIds(userIds),    UserDetailResponseDto::getUserId);
        Map<Long, PaymentDTO> paymentMap = toMap(paymentClient.getPaymentsByOrderIds(orderIds), PaymentDTO::getOrderId);

        // 4. Fetch shipments (city for display)
        Map<Long, Shipment> shipmentMap = shipmentRepository
                .findByOrderIdIn(orderIds).stream()
                .collect(Collectors.toMap(Shipment::getOrderId, Function.identity()));

        // 5. Map to summary DTOs
        return page.map(o -> {
            UserDetailResponseDto    user    = userMap.getOrDefault(o.getUserId(),    new UserDetailResponseDto());
            PaymentDTO payment = paymentMap.getOrDefault(o.getOrderId(), new PaymentDTO());
            Shipment   ship    = shipmentMap.get(o.getOrderId());

            return AdminOrderSummaryDTO.builder()
                    .orderId(o.getOrderId())
                    .razorpayOrderId(o.getRazorpayOrderId())
                    .totalAmount(o.getTotalAmount())
                    .paymentMode(o.getPaymentMode())
                    .orderStatus(o.getStatus())
                    .createdAt(o.getCreatedAt())
                    .itemCount(o.getItems() == null ? 0 : o.getItems().size())
                    .userId(o.getUserId())
                    .customerName(user.getName())
                    .customerEmail(user.getEmail())
                    .paymentStatus(payment.getStatus())
                    .shipmentStatus(ship != null && ship.getCurrentStatus() != null
                            ? ship.getCurrentStatus().name() : null)
                    .city(ship != null && ship.getShippingAddress() != null
                            ? ship.getShippingAddress().getCity() : null)
                    .build();
        });
    }

    // ── Single order detail ────────────────────────────────────────────────────
    public AdminOrderDTO getOrderDetail(Long orderId) {

        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Fetch supporting data
        UserDetailResponseDto    user    = userClient.getUserById(order.getUserId());
        PaymentDTO payment = paymentClient
                .getPaymentsByOrderIds(List.of(orderId))
                .stream().findFirst().orElse(new PaymentDTO());

        Shipment ship = shipmentRepository.findByOrderId(orderId).orElse(null);

        // Enrich order items with product info
        List<Long> variantIds = order.getItems().stream()
                .map(OrderItem::getProductVariantId).toList();

        Map<Long, ProductVariantUserOrderResponseDto> variantMap =
                toMap(productVariantClient.getVariantsByIds(variantIds),
                        ProductVariantUserOrderResponseDto::getProductVariantId);

        List<AdminOrderDTO.AdminOrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> {
                    ProductVariantUserOrderResponseDto v = variantMap.getOrDefault(
                            item.getProductVariantId(), new ProductVariantUserOrderResponseDto());
                    return AdminOrderDTO.AdminOrderItemDTO.builder()
                            .orderItemId(item.getId())
                            .productVariantId(item.getProductVariantId())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getPrice())
                            .lineTotal(item.getPrice()
                                    .multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                            .productName(v.getProductName())
                            .skuCode(v.getSkuCode())
                            .brand(v.getBrand())
                            .color(v.getColor())
                            .size(v.getSize())
                            .imageUrl(v.getImageUrl())
                            .build();
                }).toList();

        AdminOrderDTO.ShipmentInfo shipInfo = null;
        if (ship != null) {
            ShippingAddress addr = ship.getShippingAddress();
            shipInfo = AdminOrderDTO.ShipmentInfo.builder()
                    .shipmentId(ship.getShipmentId())
                    .currentStatus(ship.getCurrentStatus())
                    .estimatedDeliveryDate(ship.getEstimatedDeliveryDate() != null
                            ? ship.getEstimatedDeliveryDate().toString() : null)
                    .shippingCost(ship.getShippingCost())
                    .fullName(addr != null ? addr.getFullName() : null)
                    .mobileNumber(addr != null ? addr.getMobileNumber() : null)
                    .addressLine(addr != null ? addr.getAddressLine() : null)
                    .city(addr != null ? addr.getCity() : null)
                    .state(addr != null ? addr.getState() : null)
                    .pincode(addr != null ? addr.getPincode() : null)
                    .country(addr != null ? addr.getCountry() : null)
                    .build();
        }

        return AdminOrderDTO.builder()
                .orderId(order.getOrderId())
                .razorpayOrderId(order.getRazorpayOrderId())
                .totalAmount(order.getTotalAmount())
                .paymentMode(order.getPaymentMode())
                .orderStatus(order.getStatus())
                .createdAt(order.getCreatedAt())
                .userId(order.getUserId())
                .customerName(user.getName())
                .customerEmail(user.getEmail())
                .customerPhone(user.getCountryCode() + " " + user.getPhone())
                .items(itemDTOs)
                .shipment(shipInfo)
                .paymentStatus(payment.getStatus())
                .paymentId(payment.getPaymentId())
                .build();
    }

    // ── Update order status ────────────────────────────────────────────────────
    public AdminOrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setStatus(newStatus);
        orderRepository.save(order);
        return getOrderDetail(orderId);
    }

    // ── Status counts for stat cards ──────────────────────────────────────────
    public Map<String, Long> getStatusCounts() {
        return orderRepository.countByStatus().stream()
                .collect(Collectors.toMap(
                        arr -> ((OrderStatus) arr[0]).name(),
                        arr -> (Long) arr[1]
                ));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private <K, V> Map<K, V> toMap(List<V> list, Function<V, K> keyFn) {
        if (list == null) return Collections.emptyMap();
        return list.stream().collect(Collectors.toMap(keyFn, Function.identity(),
                (a, b) -> a)); // keep first on duplicate key
    }
}