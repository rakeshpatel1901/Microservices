package com.learning.orderservice.dto;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ProductVariantRequestDto {
    Long productVariantId;
    BigDecimal sellingPrice;
    Integer stock;
}

