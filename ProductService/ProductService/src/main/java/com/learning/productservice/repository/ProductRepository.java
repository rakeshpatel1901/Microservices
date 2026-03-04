package com.learning.productservice.repository;

import com.learning.productservice.dto.ProductCardResponseDTO;
import com.learning.productservice.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>{

    @Query(value = """
    SELECT new com.learning.productservice.dto.ProductCardResponseDTO(
        p.productId,
        p.name,
        p.description,
        c.categoryName,
        i.imageUrl,
        MAX(v.mrp),
        MIN(v.sellingPrice),
        CASE WHEN SUM(v.stock) > 0 THEN true ELSE false END
    )
    FROM Product p
    JOIN p.category c
    JOIN p.productVariants v
    JOIN v.productImages i
    WHERE i.isPrimary = true
    AND v.productVariantId = (
        SELECT MIN(v2.productVariantId)
        FROM ProductVariant v2
        WHERE v2.product = p
          AND v2.sellingPrice = (
              SELECT MIN(v3.sellingPrice)
              FROM ProductVariant v3
              WHERE v3.product = p
          )
    )
    AND (:categoryId IS NULL OR c.categoryId = :categoryId)
    AND (
         :keyword IS NULL
         OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    GROUP BY p.productId, p.name, p.description, c.categoryName, i.imageUrl
    ORDER BY
        CASE WHEN :sortBy = 'sellingPrice' AND :order = 'asc' THEN MIN(v.sellingPrice) END ASC,
        CASE WHEN :sortBy = 'sellingPrice' AND :order = 'desc' THEN MIN(v.sellingPrice) END DESC,
        CASE WHEN :sortBy = 'mrp' AND :order = 'asc' THEN MAX(v.mrp) END ASC,
        CASE WHEN :sortBy = 'mrp' AND :order = 'desc' THEN MAX(v.mrp) END DESC,
        CASE WHEN :sortBy = 'name' AND :order = 'asc' THEN p.name END ASC,
        CASE WHEN :sortBy = 'name' AND :order = 'desc' THEN p.name END DESC,
        CASE WHEN :sortBy = 'productId' AND :order = 'asc' THEN p.productId END ASC,
        CASE WHEN :sortBy = 'productId' AND :order = 'desc' THEN p.productId END DESC
           
    """,
            countQuery = """
    SELECT COUNT(DISTINCT p.productId)
    FROM Product p
    JOIN p.category c
    JOIN p.productVariants v
    JOIN v.productImages i
    WHERE i.isPrimary = true
    AND (:categoryId IS NULL OR c.categoryId = :categoryId)
    AND (
         :keyword IS NULL
         OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    """
    )
    Page<ProductCardResponseDTO> findProductCards(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable,
            String sortBy,
            String order
    );

}
