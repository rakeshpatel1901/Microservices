package com.learning.productservice.mapper;


import com.learning.productservice.dto.ProductVariantOrderResponseDto;
import com.learning.productservice.dto.ProductVariantRequestDTO;
import com.learning.productservice.dto.ProductVariantResponseDTO;
import com.learning.productservice.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring" , uses = ProductImagesMapper.class)
public interface ProductVariantMapper {

    @Mapping(source="productImages", target = "productImagesList")
    ProductVariantResponseDTO toProductVariantResponseDTO(ProductVariant productVariant);

    List<ProductVariantResponseDTO> toProductVariantResponseDTOList(List<ProductVariant> productVariantList);

    ProductVariantOrderResponseDto toProductVariantOrderResponseDto(ProductVariant productVariant);

    @Mapping(source="productImagesList" , target="productImages", ignore = true)
    ProductVariant toProductVariant(ProductVariantRequestDTO productVariantRequestDTO);
}
