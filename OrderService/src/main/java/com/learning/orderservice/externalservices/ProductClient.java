package com.learning.orderservice.externalservices;


import com.learning.orderservice.dto.ApiResponseDto;
import com.learning.orderservice.dto.ProductVariantRequestDto;
import com.learning.orderservice.dto.ProductVariantUserOrderResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @GetMapping("/api/v1/products/productVariant/{productVariantId}/{quantity}")
    public ApiResponseDto<ProductVariantRequestDto> reserveStockProductVariantById(@PathVariable Long productVariantId,
                                                                                   @PathVariable Integer quantity);


    @GetMapping("/api/v1/products/productVariant/unreserve/{productVariantId}/{quantity}")
    public ApiResponseDto<ProductVariantRequestDto> unreserveStockProductVariantById(@PathVariable Long productVariantId,
                                                                                   @PathVariable Integer quantity);


    @GetMapping("/api/v1/products/productVariant/getVariant/{id}")
    ProductVariantUserOrderResponseDto getVariant(@PathVariable("id") Long productVariantId);

    // Bulk fetch by variantId list — one call per order page
    @GetMapping("/api/v1/products/productVariant/variants/batch")
    List<ProductVariantUserOrderResponseDto> getVariantsByIds(@RequestParam("ids") List<Long> variantIds);
}
