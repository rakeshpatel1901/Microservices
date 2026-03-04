package com.payment.payment_service.external;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="ORDER-SERVICE")
public interface UserOrderClient {

    @PostMapping("/api/v1/orders/payment-success/{razorpayOrderId}/{paymentId}")
    void paymentSuccessHandle(@PathVariable String razorpayOrderId,@PathVariable String paymentId);

    @PostMapping("/api/v1/orders/payment-failure/{razorpayOrderId}")
    void paymentFailureHandle(@PathVariable String razorpayOrderId);

}
