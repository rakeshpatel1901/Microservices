package com.learning.orderservice.service.implementation;

import com.learning.orderservice.dto.*;
import com.learning.orderservice.dto.admin.AdminOrderResponseDto;
import com.learning.orderservice.dto.types.OrderStatus;
import com.learning.orderservice.dto.types.ShipmentStatus;
import com.learning.orderservice.entity.*;
import com.learning.orderservice.externalservices.AuthClient;
import com.learning.orderservice.externalservices.PaymentClient;
import com.learning.orderservice.externalservices.ProductClient;
import com.learning.orderservice.mapper.OrderMapper;
import com.learning.orderservice.repository.OrderRepository;
import com.learning.orderservice.repository.ShipmentRepository;
import com.learning.orderservice.repository.ShipmentStatusRepository;
import com.learning.orderservice.repository.ShippingAddressRepository;
import com.learning.orderservice.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final PaymentClient paymentClient;

    private final AuthClient authClient;

    private final ProductClient productClient;
    private final ShippingAddressRepository shippingAddressRepository;
    public final ShipmentRepository shipmentRepository;
    private final ShipmentStatusRepository shipmentStatusRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderMapper orderMapper,
                            PaymentClient paymentClient,
                            AuthClient authClient,
                            ProductClient productClient,
                            ShippingAddressRepository shippingAddressRepository,
                            ShipmentRepository shipmentRepository,
                            ShipmentStatusRepository shipmentStatusRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.paymentClient = paymentClient;
        this.authClient = authClient;
        this.productClient = productClient;
        this.shippingAddressRepository = shippingAddressRepository;
        this.shipmentRepository = shipmentRepository;
        this.shipmentStatusRepository = shipmentStatusRepository;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto request) throws Exception {

        UserDetailResponseDto userDetailResponseDto = authClient.getMe();

        Orders order = new Orders();
        order.setUserId(userDetailResponseDto.getUserId());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentMode(request.getPaymentMode()); // ✅ NEW

        List<OrderItem> orderItems = new ArrayList<>();
        List<OrderItemRequestDto> reservedItems = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;

        try {

            for (OrderItemRequestDto item : request.getItems()) {

                ApiResponseDto<ProductVariantRequestDto> response =
                        productClient.reserveStockProductVariantById(
                                item.getProductVariantId(),
                                item.getQuantity()
                        );

                if (!response.getStatus().equalsIgnoreCase("SUCCESS")) {
                    throw new RuntimeException("Stock not available for product: " + item.getProductVariantId());
                }

                reservedItems.add(item);

                ProductVariantRequestDto product = response.getData();

                OrderItem orderItem = new OrderItem();
                orderItem.setProductVariantId(product.getProductVariantId());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setPrice(product.getSellingPrice());
                orderItem.setOrder(order);

                orderItems.add(orderItem);

                totalAmount = totalAmount.add(
                        product.getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                );
            }

        } catch (Exception e) {

            // rollback reserved stock
            for (OrderItemRequestDto item : reservedItems) {
                productClient.unreserveStockProductVariantById(
                        item.getProductVariantId(),
                        item.getQuantity()
                );
            }

            throw new RuntimeException("Order failed: " + e.getMessage());
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);


        orderRepository.save(order);


        AddressDto addr = request.getAddress();

        ShippingAddress address = new ShippingAddress();
        address.setFullName(userDetailResponseDto.getName());
        address.setMobileNumber(userDetailResponseDto.getPhone());
        address.setAddressLine(addr.getAddressLine());
        address.setCity(addr.getCity());
        address.setState(addr.getState());
        address.setPincode(addr.getPincode());
        address.setCountry(addr.getCountry());

        shippingAddressRepository.save(address);

        // =========================================================
        // ✅ STEP 3: CREATE SHIPMENT
        // =========================================================

        Shipment shipment = new Shipment();
        shipment.setOrderId(order.getOrderId());
        shipment.setShippingAddress(address);
        shipment.setShippingCost(0.0);
        shipment.setWeight(0.0);
        shipment.setEstimatedDeliveryDate(LocalDate.now().plusDays(7));

        shipmentRepository.save(shipment);

        // ✅ Shipment status history
        ShipmentStatusHistory history = new ShipmentStatusHistory();
        history.setShipment(shipment);
        history.setStatus(ShipmentStatus.CREATED);
        history.setTimestamp(LocalDateTime.now());

        shipmentStatusRepository.save(history);

        // =========================================================
        // ✅ STEP 4: PAYMENT HANDLING
        // =========================================================

        OrderResponseDto dto = new OrderResponseDto();
        if ("COD".equalsIgnoreCase(request.getPaymentMode())) {


            boolean result = paymentClient.createOrderOffline(order.getOrderId(), totalAmount);
            order.setStatus(OrderStatus.PENDING); // no payment needed

        } else if("ONLINE".equalsIgnoreCase(request.getPaymentMode())) {


            RazorpayResponseDto paymentResponse =
                    paymentClient.createOrder(order.getOrderId(), totalAmount);
            dto.setAmount(paymentResponse.getAmount());
            dto.setRazorpayOrderId(paymentResponse.getRazorpayOrderId());
            order.setStatus(OrderStatus.PENDING);
            order.setRazorpayOrderId(paymentResponse.getRazorpayOrderId());
        }

        orderRepository.save(order);

        dto.setOrderId(order.getOrderId());
        return dto;
    }

    @Override
    public Boolean hasUserPurchasedProduct(Long productVariantId) {
        Long userId = authClient.getUserId();
        return orderRepository.existsByUserAndProduct(userId, productVariantId);
    }


    @Override
    public List<UserOrderResponseDto> getOrdersForUser() {

        Long userId = authClient.getUserId();

        List<Orders> orders = orderRepository.findByUserId(userId);

        return orders.stream().map(order -> {

            UserOrderResponseDto dto = new UserOrderResponseDto();

            dto.setOrderId(order.getOrderId().toString());
            dto.setDate(order.getCreatedAt().toLocalDate().toString());
            dto.setStatus(mapStatus(order.getStatus()));
            dto.setTotal(order.getTotalAmount());
            dto.setPaymentMethod(order.getPaymentMode());

            // ─── Shipment ───
            Shipment shipment = shipmentRepository.findByOrderId(order.getOrderId()).orElse(null);

            if (shipment != null) {

                // Address
                ShippingAddress addr = shipment.getShippingAddress();
                dto.setAddress(
                        addr.getAddressLine() + ", " +
                                addr.getCity() + ", " +
                                addr.getState() + " " +
                                addr.getPincode()
                );

                // Timeline
                List<ShipmentStatusHistory> history =
                        shipmentStatusRepository.findByShipment_ShipmentIdOrderByTimestampAsc(
                                shipment.getShipmentId()
                        );

                List<TimelineDto> timeline = history.stream().map(h -> {
                    TimelineDto t = new TimelineDto();
                    t.setLabel(formatStatus(h.getStatus()));
                    t.setDate(h.getTimestamp().toLocalDate().toString());
                    t.setDone(true);
                    return t;
                }).toList();

                dto.setTimeline(timeline);
            }

            // ─── Items ───
            List<OrderItemDto> items = order.getItems().stream().map(item -> {

                OrderItemDto i = new OrderItemDto();
                i.setQty(item.getQuantity());
                i.setPrice(item.getPrice());

                // Call Product Service
                ProductVariantUserOrderResponseDto product =
                        productClient.getVariant(item.getProductVariantId());

                i.setName(product.getProductName());
                i.setImageUrl(product.getImageUrl());

                return i;

            }).toList();

            dto.setItems(items);

            return dto;

        }).toList();
    }
    private String formatStatus(ShipmentStatus status) {
        return switch (status) {
            case CREATED -> "Order Placed";
            case CONFIRMED -> "Confirmed";
            case SHIPPED -> "Shipped";
            case OUT_FOR_DELIVERY -> "Out for Delivery";
            case DELIVERED -> "Delivered";
        };
    }
    private String mapStatus(OrderStatus status) {
        return switch (status) {
            case PROCESSING -> "processing";
            case PENDING -> "pending";
            case SHIPPED -> "shipped";
            case DELIVERED -> "delivered";
            case CANCELLED -> "cancelled";
            case REFUNDED -> "refunded";
            case RETURNED -> "returned";
        };
    }


//    public List<AdminOrderResponseDto> getAllAdminOrders() {
//
//        List<Orders> orders = orderRepository.findAll();
//
//        return orders.stream().map(order -> {
//
//            AdminOrderResponseDto dto = new AdminOrderResponseDto();
//
//            // Order basic
//            dto.setId("#ORD-" + order.getOrderId());
//            dto.setAmount(order.getTotalAmount().doubleValue());
//            dto.setStatus(order.getStatus().name());
//            dto.setDate(order.getCreatedAt().toLocalDate().toString());
//            dto.setPayment(order.getPaymentMode());
//
//            // User
//            Users user = userRepository.findById(order.getUserId()).orElse(null);
//            if (user != null) {
//                dto.setCustomer(user.getName());
//                dto.setEmail(user.getEmail());
//                dto.setPhone(user.getPhone());
//            }
//
//            // Order Items (taking first item for UI)
//            OrderItem item = order.getItems().get(0);
//            dto.setQty(item.getQuantity());
//
//            // Product
//            ProductVariant variant = productVariantRepository
//                    .findById(item.getProductVariantId()).orElse(null);
//
//            if (variant != null && variant.getProduct() != null) {
//                dto.setProduct(variant.getProduct().getName());
//            }
//
//            // Shipment
//            Shipment shipment = shipmentRepository.findByOrderId(order.getOrderId());
//            if (shipment != null && shipment.getShippingAddress() != null) {
//                dto.setAddress(shipment.getShippingAddress().getCity());
//            }
//
//            // Payment
//            Payments payment = paymentRepository.findByOrderId(order.getOrderId());
//            if (payment != null) {
//                dto.setPayStatus(payment.getStatus().name());
//            }
//
//            return dto;
//
//        }).toList();
//    }
}
