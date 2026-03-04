package com.learning.orderservice.service;

import com.learning.orderservice.dto.ApiResponseDto;
import com.learning.orderservice.dto.OrderResponseDto;
import com.learning.orderservice.dto.ProductVariantRequestDto;

public interface OrderService {
    OrderResponseDto createOrder(Integer quantity, ApiResponseDto<ProductVariantRequestDto> apiResponseDto) throws Exception;
}
