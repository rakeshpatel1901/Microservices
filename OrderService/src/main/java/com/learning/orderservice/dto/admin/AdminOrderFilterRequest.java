package com.learning.orderservice.dto.admin;


import com.learning.orderservice.dto.types.OrderStatus;
import lombok.Data;

@Data
public class AdminOrderFilterRequest {
    private OrderStatus status;     // null = All
    private String search;          // orderId / customerName partial match
    private int page = 0;           // 0-indexed
    private int size = 10;          // rows per page (10 or 20)
}