package com.learning.productservice.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProductImageRequestDTO {
    private MultipartFile imageUrl;
    private Boolean isPrimary;
}
