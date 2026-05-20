package com.mahalaxmi.review.externalservices;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderClient {

    @GetMapping("/api/v1/orders/verify")
    Boolean hasUserPurchasedProduct(
            @RequestParam Long productVariantId);
}