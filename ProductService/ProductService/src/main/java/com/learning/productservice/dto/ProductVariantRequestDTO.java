package com.learning.productservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductVariantRequestDTO {
    private String color;
    private String size;
    private String brand;
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    private BigDecimal buyingPrice;
    private Integer stock;
    private List<ProductImageRequestDTO> productImagesList;
}
