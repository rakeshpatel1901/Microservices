package com.learning.orderservice.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {
    private String name;
    private Integer qty;
    private BigDecimal price;
    private String imageUrl;
}
