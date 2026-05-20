package com.learning.orderservice.externalservices;


import com.learning.orderservice.config.FeignConfig;
import com.learning.orderservice.dto.PaymentDTO;
import com.learning.orderservice.dto.RazorpayResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name="payment-service", configuration = FeignConfig.class)
public interface PaymentClient {

    @PostMapping("/api/payment/create-online/{orderId}")
    RazorpayResponseDto createOrder(@PathVariable Long orderId,
                                                    @RequestParam BigDecimal amount) throws Exception;

    @PostMapping("/api/payment/create-offline/{orderId}")
    boolean createOrderOffline(@PathVariable Long orderId,
                               @RequestParam BigDecimal amount) throws Exception;

    @GetMapping("/api/payment/by-orders")
    List<PaymentDTO> getPaymentsByOrderIds(@RequestParam("orderIds") List<Long> orderIds);
}
