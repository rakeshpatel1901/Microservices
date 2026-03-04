package com.learning.productservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;



@Getter
@Setter
public class ProductVariantOrderResponseDto {
    private Long productVariantId;
    private BigDecimal sellingPrice;
    private Integer stock;
    private boolean isAvailable;
}
