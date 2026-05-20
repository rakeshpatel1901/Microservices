package com.learning.orderservice.dto.admin;



import com.learning.orderservice.dto.types.OrderStatus;
import com.learning.orderservice.dto.types.ShipmentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminOrderDTO {

    // ── Order core ──────────────────────────────────────────
    private Long orderId;
    private String razorpayOrderId;
    private BigDecimal totalAmount;
    private String paymentMode;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;

    // ── Customer (fetched from AuthService via Feign) ───────
    private Long userId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    // ── Line items (enriched from ProductService via Feign) ─
    private List<AdminOrderItemDTO> items;

    // ── Shipment ────────────────────────────────────────────
    private ShipmentInfo shipment;

    // ── Payment (fetched from PaymentService via Feign) ─────
    private String paymentStatus;   // e.g. SUCCESS / PENDING / REFUNDED
    private String paymentId;       // Razorpay payment ID

    // ── Nested: one order item ───────────────────────────────
    @Data
    @Builder
    public static class AdminOrderItemDTO {
        private Long orderItemId;
        private Long productVariantId;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;   // unitPrice × quantity

        // Enriched from ProductService
        private String productName;
        private String skuCode;
        private String brand;
        private String color;
        private String size;
        private String imageUrl;        // primary image URL
    }

    // ── Nested: shipment + address ───────────────────────────
    @Data
    @Builder
    public static class ShipmentInfo {
        private Long shipmentId;
        private ShipmentStatus currentStatus;
        private String estimatedDeliveryDate;
        private Double shippingCost;

        // ShippingAddress fields
        private String fullName;
        private String mobileNumber;
        private String addressLine;
        private String city;
        private String state;
        private String pincode;
        private String country;
    }
}