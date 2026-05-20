package com.learning.productservice.service.implementation;

import com.learning.productservice.dto.CategoryNameDto;
import com.learning.productservice.dto.CategoryResponseDTO;
import com.learning.productservice.entity.Categories;
import com.learning.productservice.exception.CategoryNotExists;
import com.learning.productservice.mapper.CategoriesMapper;
import com.learning.productservice.repository.CategoryRepository;
import com.learning.productservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoriesMapper categoriesMapper;


    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        List<Categories> categories = categoryRepository.findAll();
        return categoriesMapper.toCategoryResponseDtoList(categories);
    }

    @Override
    public CategoryResponseDTO getCategoryById(Long categoryId) {
        Categories categories = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotExists("No Category Found"));
        return categoriesMapper.toCategoryResponseDto(categories);
    }

    @Override
    public List<CategoryNameDto> getAllCategoriesName() {
        List<Categories> categories = categoryRepository.findAll();
        return categoriesMapper.toCategoryNameDtoList(categories);
    }
}
