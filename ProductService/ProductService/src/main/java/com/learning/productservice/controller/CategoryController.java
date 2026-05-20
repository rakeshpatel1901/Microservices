package com.learning.productservice.controller;


import com.learning.productservice.dto.CategoryNameDto;
import com.learning.productservice.dto.CategoryResponseDTO;
import com.learning.productservice.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories(){
        List<CategoryResponseDTO> categoryResponseDTOS = categoryService.getAllCategories();
        return ResponseEntity.ok(categoryResponseDTOS);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long categoryId){
        CategoryResponseDTO categoryResponseDTO = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(categoryResponseDTO);
    }

    @GetMapping("/names")
    public ResponseEntity<List<CategoryNameDto>> getAllCategoriesName(){
        List<CategoryNameDto> categoryNameDtos = categoryService.getAllCategoriesName();
        return ResponseEntity.ok(categoryNameDtos);
    }


}
