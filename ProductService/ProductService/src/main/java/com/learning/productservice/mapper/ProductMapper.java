package com.learning.productservice.mapper;

import com.learning.productservice.dto.ProductDetailDTO;
import com.learning.productservice.dto.ProductResponseDTO;
import com.learning.productservice.entity.Product;
import com.learning.productservice.entity.ProductImages;
import com.learning.productservice.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductVariantMapper.class})
public interface ProductMapper {

    ProductResponseDTO toProductResponseDto(Product products);
    List<ProductResponseDTO> toProductResponseDtoList(List<Product> productsList);


    @Mapping(source = "category", target = "categoryResponseDTO")
    ProductDetailDTO toProductDetailDTO(Product products);

}
