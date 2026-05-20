package com.learning.orderservice.dto;

import com.learning.orderservice.dto.types.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderResponseDto {
    private long orderId;
    private long amount;
    private String razorpayOrderId;

}
