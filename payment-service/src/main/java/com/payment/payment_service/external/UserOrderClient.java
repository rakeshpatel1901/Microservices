package com.payment.payment_service.external;


import com.payment.payment_service.configuration.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="ORDER-SERVICE", configuration = FeignConfig.class)
public interface UserOrderClient {

    @PostMapping("/api/v1/orders/payment-success/{razorpayOrderId}/{paymentId}/{paymentMode}")
    void paymentSuccessHandle(@PathVariable String razorpayOrderId,@PathVariable Long paymentId,
                              @PathVariable String paymentMode);

    @PostMapping("/api/v1/orders/payment-failure/{razorpayOrderId}")
    void paymentFailureHandle(@PathVariable String razorpayOrderId);

}
