package com.learning.productservice.mapper;


import com.learning.productservice.dto.CategoryNameDto;
import com.learning.productservice.dto.CategoryResponseDTO;
import com.learning.productservice.entity.Categories;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring" , uses = ProductMapper.class)
public interface CategoriesMapper {

    @Mapping(source = "products" , target = "productResponseDTOList")
    CategoryResponseDTO toCategoryResponseDto(Categories categories);

    List<CategoryResponseDTO> toCategoryResponseDtoList(List<Categories> categoriesList);

    @Mapping(source = "categoryName" , target = "categoryName")
    CategoryNameDto toCategoryNameDto(Categories categories);
    List<CategoryNameDto> toCategoryNameDtoList(List<Categories> categoriesList);

}
