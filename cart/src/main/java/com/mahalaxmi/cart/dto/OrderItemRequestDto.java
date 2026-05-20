package com.mahalaxmi.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequestDto {
    private Long productVariantId;
    private Integer quantity;
}
