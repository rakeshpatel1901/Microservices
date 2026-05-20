package com.mahalaxmi.cart.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderResponseDto {
    private long orderId;
    private BigDecimal amount;
    private String razorpayOrderId;
    private String paymentId;

}
