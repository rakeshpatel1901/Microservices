package com.mahalaxmi.cart.externalservices;


import com.mahalaxmi.cart.dto.ProductVariantCartResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @PostMapping("/api/v1/products/productVariant/bulk")
    List<ProductVariantCartResponseDto> getVariantsByIds(@RequestBody List<Long> ids);
}