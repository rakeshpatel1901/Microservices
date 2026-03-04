package com.learning.productservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductVariantResponseDTO {
    private String color;
    private String size;
    private String brand;
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    private Integer stock;
    private String skuCode;
    private List<ProductImagesResponseDTO> productImagesList;
}
