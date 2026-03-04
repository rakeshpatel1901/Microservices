package com.learning.productservice.mapper;

import com.learning.productservice.dto.ProductImagesResponseDTO;
import com.learning.productservice.entity.ProductImages;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImagesMapper {


    ProductImagesResponseDTO toProductImageResponseDTO(ProductImages productImages);
}
