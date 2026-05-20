package com.learning.orderservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UserOrderResponseDto {




    private String orderId;
    private String date;
    private String status;
    private BigDecimal total;
    private String paymentMethod;
    private String address;

    private List<OrderItemDto> items;
    private List<TimelineDto> timeline;
}