package com.payment.payment_service.service.implementation;

import com.payment.payment_service.dto.RazorpayResponseDto;
import com.payment.payment_service.dto.types.PaymentStatus;
import com.payment.payment_service.entity.Payments;
import com.payment.payment_service.repository.PaymentRepository;
import com.payment.payment_service.service.RazorpayService;
import com.razorpay.Order;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@Slf4j

public class RazorpayServiceImpl implements RazorpayService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;

    @Autowired
    public RazorpayServiceImpl(RazorpayClient razorpayClient, PaymentRepository paymentRepository){
        this.razorpayClient = razorpayClient;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public RazorpayResponseDto createPaymentOrder(Long orderId, BigDecimal amount) throws RazorpayException {

        JSONObject options = new JSONObject();
        options.put("amount", amount.multiply(BigDecimal.valueOf(100)));
        options.put("currency", "INR");
        options.put("receipt","order_"+orderId);


        Order razorpay = razorpayClient.orders.create(options);

        Payments payment = new Payments();
        payment.setOrderId(orderId);
        payment.setRazorpayOrderId(razorpay.get("id"));
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.CREATED);

        paymentRepository.save(payment);
        Number amountNumber = (Number) razorpay.get("amount");

        return new RazorpayResponseDto(
                razorpay.get("id"),
                amountNumber.longValue(),
                razorpay.get("currency")
        );

    }
}
