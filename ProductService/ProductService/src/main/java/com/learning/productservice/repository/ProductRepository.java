package com.learning.productservice.repository;

import com.learning.productservice.dto.ProductCardResponseDTO;
import com.learning.productservice.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            SELECT new com.learning.productservice.dto.ProductCardResponseDTO(
                p.productId,
                p.name,
                p.description,
                c.categoryName,
                i.imageUrl,
                MAX(v.mrp),
                MIN(v.sellingPrice),
                CASE WHEN SUM(v.stock) > 0 THEN 1L ELSE 0L END
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
            """)
    Page<ProductCardResponseDTO> findProductCards(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query(value = """
            SELECT 
                p.product_id,
                p.name,
                p.description,
                c.category_name,
            
                (
                    SELECT pi.image_url
                    FROM product_images pi
                    JOIN product_variant pv2 ON pi.product_variant_id = pv2.product_variant_id
                    WHERE pv2.product_id = p.product_id AND pi.is_primary = true
                    LIMIT 1
                ) AS imageUrl,
            
                (
                    SELECT MIN(pv3.mrp)
                    FROM product_variant pv3
                    WHERE pv3.product_id = p.product_id
                ) AS mrp,
            
                (
                    SELECT MIN(pv4.selling_price)
                    FROM product_variant pv4
                    WHERE pv4.product_id = p.product_id
                ) AS sellingPrice,
            
                (
                    EXISTS (
                        SELECT 1 FROM product_variant pv5
                        WHERE pv5.product_id = p.product_id AND pv5.stock > 0
                    )
                ) AS inStock
            
            FROM products p
            JOIN categories c ON p.category_id = c.category_id
            WHERE c.category_id = :categoryId
            """, nativeQuery = true)
    List<ProductCardResponseDTO> findByCategory_CategoryId(Long categoryId);




    // ── Paginated list with optional category + search filters ───────────────
    @Query(
            value =
                    "SELECT DISTINCT p FROM Product p " +
                            "LEFT JOIN FETCH p.productVariants v " +
                            "WHERE (:categoryId IS NULL OR p.category.categoryId = :categoryId) " +
                            "AND (:search IS NULL " +
                            "     OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
                            "     OR EXISTS (" +
                            "         SELECT 1 FROM ProductVariant sv " +
                            "         WHERE sv.product = p " +
                            "         AND LOWER(sv.skuCode) LIKE LOWER(CONCAT('%', :search, '%'))" +
                            "     )" +
                            ")",
            countQuery =
                    "SELECT COUNT(DISTINCT p) FROM Product p " +
                            "LEFT JOIN p.productVariants v " +
                            "WHERE (:categoryId IS NULL OR p.category.categoryId = :categoryId) " +
                            "AND (:search IS NULL " +
                            "     OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
                            "     OR EXISTS (" +
                            "         SELECT 1 FROM ProductVariant sv " +
                            "         WHERE sv.product = p " +
                            "         AND LOWER(sv.skuCode) LIKE LOWER(CONCAT('%', :search, '%'))" +
                            "     )" +
                            ")"
    )
    Page<Product> findAllWithFilters(
            @Param("categoryId") Long     categoryId,
            @Param("search")     String   search,
            Pageable pageable
    );

    // ── Stat card counts ─────────────────────────────────────────────────────

    @Query(
            "SELECT COUNT(DISTINCT p.productId) FROM Product p " +
                    "JOIN p.productVariants v " +
                    "GROUP BY p.productId " +
                    "HAVING SUM(v.stock) > 0"
    )
    long countInStock();

    @Query(
            "SELECT COUNT(DISTINCT p.productId) FROM Product p " +
                    "JOIN p.productVariants v " +
                    "GROUP BY p.productId " +
                    "HAVING SUM(v.stock) = 0"
    )
    long countOutOfStock();

    @Query(
            "SELECT COUNT(DISTINCT p.productId) FROM Product p " +
                    "JOIN p.productVariants v " +
                    "GROUP BY p.productId " +
                    "HAVING SUM(v.stock) BETWEEN 1 AND 3"
    )
    long countLowStock();
}
