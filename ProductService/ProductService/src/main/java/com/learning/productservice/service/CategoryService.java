package com.learning.productservice.service;

import com.learning.productservice.dto.CategoryNameDto;
import com.learning.productservice.dto.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    public List<CategoryResponseDTO> getAllCategories();
    public CategoryResponseDTO getCategoryById(Long categoryId);
    public List<CategoryNameDto> getAllCategoriesName();
}
