package com.learning.productservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDetailDTO {
    Long productId;

    String name;

    String description;

    CategoryResponseDTO categoryResponseDTO;

    List<ProductVariantResponseDTO> productVariantsList;
}
