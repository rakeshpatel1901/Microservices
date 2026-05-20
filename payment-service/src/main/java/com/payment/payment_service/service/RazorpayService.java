package com.payment.payment_service.service;

import com.payment.payment_service.dto.PaymentDTO;
import com.payment.payment_service.dto.RazorpayResponseDto;
import com.razorpay.RazorpayException;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

public interface RazorpayService {
    public RazorpayResponseDto createPaymentOrder(Long orderId, BigDecimal amount) throws RazorpayException;
    public boolean createPaymentOrderOffline(Long orderId, BigDecimal amount) throws RazorpayException;
    List<PaymentDTO> getPaymentsByOrderIds(List<Long> orderIds);
}
