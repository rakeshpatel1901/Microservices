package com.mahalaxmi.cart.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItemDto {
    private Long productVariantId;
    private Integer quantity;
    private String productName;
    private String variantName;
    private BigDecimal sellingPrice;
    private BigDecimal mrp;
    private String imageUrl;
    private String brand;
}
