package com.payment.payment_service.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentVerifyDto {
    private String razorpayOrderId;
    private String paymentId;
    private String signature;
}
