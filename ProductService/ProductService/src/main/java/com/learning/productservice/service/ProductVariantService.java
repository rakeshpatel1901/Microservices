package com.learning.productservice.service;

import com.learning.productservice.dto.ProductVariantOrderResponseDto;

public interface ProductVariantService {

    ProductVariantOrderResponseDto reserveStock(Long productVariantId, Integer quantity);
    ProductVariantOrderResponseDto unreserveStock(Long productVariantId, Integer quantity);
}
