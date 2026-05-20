package com.learning.productservice.service;

import com.learning.productservice.dto.*;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductService {
    public List<ProductCardResponseDTO> getAllProducts(Long categoryId, String keyword, Pageable pageable, String sortBy, String order);
    public ProductDetailDTO getProductDetails(Long productId);
    public String addProduct(AddProductRequestDTO productRequestDTO);
    public String addProductVariant(ProductVariantRequestDTO productVariantRequestDTO, Long productId);
    public String addProductVariantImage(ProductImageRequestDTO productImageRequestDTO, Long productVariantId);
    public String updateProduct(Long productId, AddProductRequestDTO dto);
    public String deleteProduct(Long productId);
    public String updateProductVariant(Long variantId, ProductVariantRequestDTO dto);
    public String deleteProductVariant(Long variantId);
    public String updateProductVariantImage(Long imageId, ProductImageRequestDTO dto);
    public String deleteProductVariantImage(Long imageId);
    List<ProductCardResponseDTO> getProductsByCategory(Long categoryId, int limit);
}
