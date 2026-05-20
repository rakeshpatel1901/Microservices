package com.mahalaxmi.cart.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartResponseDto {
    private Long userId;
    private List<CartItemDto> items;
}
