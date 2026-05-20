package com.learning.productservice.repository;

import com.learning.productservice.dto.ProductVariantFlatDTO;

import com.learning.productservice.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    @Query(value = """
    SELECT 
        pv.product_variant_id AS productVariantId,
        pv.color,
        pv.size,
        pv.brand,
        pv.mrp,
        pv.selling_price AS sellingPrice,
        pv.buying_price AS buyingPrice,
        pv.stock,
        pv.sku_code AS skuCode,

        pi.product_images_id AS productImagesId,
        pi.image_url AS imageUrl,
        pi.is_primary AS isPrimary

    FROM product_variant pv
    LEFT JOIN product_images pi 
        ON pv.product_variant_id = pi.product_variant_id

    WHERE pv.product_id = :productId
""", nativeQuery = true)
    List<ProductVariantFlatDTO> findVariantsFlatByProductId(Long productId);

    List<ProductVariant> findByProductVariantIdIn(List<Long> ids);

    boolean existsBySkuCode(String skuCode);

    // Used to check SKU uniqueness during update (exclude self)
    boolean existsBySkuCodeAndProductVariantIdNot(String skuCode, Long excludeId);

}
