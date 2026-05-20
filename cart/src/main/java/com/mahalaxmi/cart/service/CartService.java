package com.mahalaxmi.cart.service;

import com.mahalaxmi.cart.dto.CartItemRequestDto;
import com.mahalaxmi.cart.dto.CartResponseDto;

public interface CartService {
    String addItem(CartItemRequestDto dto);

    CartResponseDto getCart();

    String updateItem(Long productVariantId, Integer quantity);

    String removeItem(Long productVariantId);

    String clearCart();

    CartResponseDto checkout();
}
