package com.payment.payment_service.service;

import com.payment.payment_service.dto.RazorpayResponseDto;
import com.razorpay.RazorpayException;

import java.math.BigDecimal;

public interface RazorpayService {
    public RazorpayResponseDto createPaymentOrder(Long orderId, BigDecimal amount) throws RazorpayException;
}
