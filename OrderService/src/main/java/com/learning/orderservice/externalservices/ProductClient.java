package com.learning.orderservice.externalservices;


import com.learning.orderservice.dto.ApiResponseDto;
import com.learning.orderservice.dto.ProductVariantRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @GetMapping("/api/v1/products/productVariant/{productVariantId}/{quantity}")
    public ApiResponseDto<ProductVariantRequestDto> reserveStockProductVariantById(@PathVariable Long productVariantId,
                                                                                   @PathVariable Integer quantity);


    @GetMapping("/api/v1/products/productVariant/unreserve/{productVariantId}/{quantity}")
    public ApiResponseDto<ProductVariantRequestDto> unreserveStockProductVariantById(@PathVariable Long productVariantId,
                                                                                   @PathVariable Integer quantity);



}
