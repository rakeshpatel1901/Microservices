package com.learning.productservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AddProductRequestDTO {
    String name;
    String description;
    Long categoryId;
    List<ProductVariantRequestDTO> productVariants;

}
