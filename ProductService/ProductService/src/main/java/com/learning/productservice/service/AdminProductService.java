package com.learning.productservice.service;

import com.learning.productservice.dto.Admin.*;
import org.springframework.data.domain.Page;

public interface AdminProductService {
    public AdminProductStatsDTO getStats();
    public Page<AdminProductSummaryDTO> getProducts( Long categoryId, String search, int page, int size);
    public AdminProductDetailDTO getProductDetail(Long productId);
    public AdminProductDetailDTO createProduct(CreateProductRequest req);
    public AdminProductDetailDTO updateProduct(Long productId, UpdateProductRequest req);
    public void deleteProduct(Long productId);
    public AdminProductSummaryDTO updateVariantStock(Long variantId, int newStock);
    public AdminProductSummaryDTO toggleInStock(Long productId);

}
