package com.learning.orderservice.externalservices;


import com.learning.orderservice.dto.RazorpayResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name="payment-service")
public interface PaymentClient {

    @PostMapping("/api/payment/create/{orderId}")
    RazorpayResponseDto createOrder(@PathVariable Long orderId,
                                                    @RequestParam BigDecimal amount) throws Exception;

}
