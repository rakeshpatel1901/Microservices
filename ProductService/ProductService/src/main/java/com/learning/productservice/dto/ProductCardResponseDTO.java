package com.learning.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCardResponseDTO {
    private Long productId;

    private String name;

    private String description;

    private String categoryName;

    private String imageUrl;

    private BigDecimal mrp;

    private BigDecimal sellingPrice;

    private Boolean inStock;

}
