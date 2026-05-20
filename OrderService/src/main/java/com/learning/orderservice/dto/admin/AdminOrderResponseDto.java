package com.learning.orderservice.dto.admin;

import lombok.Data;

@Data
public class AdminOrderResponseDto {

    private String id;
    private String customer;
    private String email;
    private String phone;

    private String product;
    private Integer qty;

    private Double amount;
    private String status;
    private String date;

    private String address;

    private String payment;
    private String payStatus;
}