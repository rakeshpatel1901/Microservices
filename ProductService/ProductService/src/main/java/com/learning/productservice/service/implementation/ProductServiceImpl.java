package com.learning.productservice.service.implementation;

import com.learning.productservice.dto.ProductCardResponseDTO;
import com.learning.productservice.dto.ProductDetailDTO;
import com.learning.productservice.entity.Product;
import com.learning.productservice.mapper.ProductMapper;
import com.learning.productservice.repository.ProductRepository;
import com.learning.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {


    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductMapper productMapper;

    @Override
    public List<ProductCardResponseDTO> getAllProducts(Long categoryId, String keyword, Pageable pageable, String sortBy, String order) {
        return productRepository.findProductCards(categoryId, keyword, pageable,sortBy,order).getContent();
    }

    @Override
    public ProductDetailDTO getProductDetails(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("No Such Product Exists"));
        return productMapper.toProductDetailDTO(product);
    }
}
