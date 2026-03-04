package com.learning.productservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImagesResponseDTO {
    private String imageUrl;
    private Boolean isPrimary;
}
