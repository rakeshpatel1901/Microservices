package com.mahalaxmi.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemRequestDto {
    private Long productVariantId;
    private Integer quantity;
}
