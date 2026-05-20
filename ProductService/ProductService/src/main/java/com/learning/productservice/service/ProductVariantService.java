package com.learning.productservice.service;

import com.learning.productservice.dto.ProductVariantCartResponseDto;
import com.learning.productservice.dto.ProductVariantOrderResponseDto;
import com.learning.productservice.dto.ProductVariantResponseDTO;
import com.learning.productservice.dto.ProductVariantUserOrderResponseDto;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ProductVariantService {
    public List<ProductVariantResponseDTO> getVariantsByProductId(Long productId);
    ProductVariantOrderResponseDto reserveStock(Long productVariantId, Integer quantity);
    ProductVariantOrderResponseDto unreserveStock(Long productVariantId, Integer quantity);
    public List<ProductVariantCartResponseDto> getVariantsByIds(List<Long> ids);
    public ProductVariantUserOrderResponseDto getVariantById(Long id);
    List<ProductVariantUserOrderResponseDto> getOrderVariantsByIds(List<Long> variantIds);
}
