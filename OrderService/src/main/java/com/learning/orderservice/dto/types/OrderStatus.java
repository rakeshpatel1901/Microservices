package com.learning.orderservice.dto.types;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED,
    CANCELLED,
    REFUNDED,
}
