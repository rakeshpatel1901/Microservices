package com.payment.payment_service.dto;


import lombok.Data;

@Data
public class PaymentDTO {
    private Long orderId;
    private String paymentId;
    private String status;  // SUCCESS / PENDING / FAILED / REFUNDED
}
