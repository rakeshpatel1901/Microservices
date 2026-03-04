package com.learning.productservice.service;

import com.learning.productservice.dto.ProductCardResponseDTO;
import com.learning.productservice.dto.ProductDetailDTO;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductService {
    public List<ProductCardResponseDTO> getAllProducts(Long categoryId, String keyword, Pageable pageable, String sortBy, String order);
    public ProductDetailDTO getProductDetails(Long productId);
}
