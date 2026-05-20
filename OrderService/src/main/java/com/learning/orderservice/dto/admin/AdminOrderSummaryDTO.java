package com.learning.orderservice.dto.admin;


import com.learning.orderservice.dto.types.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminOrderSummaryDTO {

    private Long orderId;
    private String razorpayOrderId;
    private BigDecimal totalAmount;
    private String paymentMode;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private int itemCount;

    // Customer info
    private Long userId;
    private String customerName;
    private String customerEmail;

    // Payment
    private String paymentStatus;

    // Shipment
    private String shipmentStatus;
    private String city;            // from ShippingAddress
}


