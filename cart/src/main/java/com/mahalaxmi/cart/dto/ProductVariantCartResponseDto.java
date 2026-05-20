package com.mahalaxmi.cart.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductVariantCartResponseDto {

    private Long productVariantId;
    private String productName;
    private String color;
    private String size;
    private BigDecimal sellingPrice;
    private BigDecimal mrp;
    private String imageUrl;
    private String brand;

}