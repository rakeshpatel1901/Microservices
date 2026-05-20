package com.learning.orderservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantUserOrderResponseDto {
    private Long productVariantId;
    private String productName;
    private String skuCode;
    private String brand;
    private String color;
    private String size;
    private String imageUrl;
    private BigDecimal sellingPrice;
    private BigDecimal mrp;
}