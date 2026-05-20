package com.learning.orderservice.service;

import com.learning.orderservice.dto.*;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto orderRequestDto) throws Exception;
    Boolean hasUserPurchasedProduct(Long productVariantId);
    public List<UserOrderResponseDto> getOrdersForUser() ;
}
