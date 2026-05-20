package com.learning.productservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryResponseDTO {

    Long categoryId;
    String categoryName;

    String description;

    List<ProductResponseDTO>  productResponseDTOList;
}
