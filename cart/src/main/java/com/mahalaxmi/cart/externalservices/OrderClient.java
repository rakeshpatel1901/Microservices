package com.mahalaxmi.cart.externalservices;

import com.mahalaxmi.cart.dto.OrderRequestDto;
import com.mahalaxmi.cart.dto.OrderResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service")
public interface OrderClient {

    @PostMapping("/api/v1/orders/place-order")
    OrderResponseDto createOrder(@RequestBody OrderRequestDto request);
}
