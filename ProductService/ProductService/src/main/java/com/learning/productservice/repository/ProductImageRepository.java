package com.learning.productservice.repository;

import com.learning.productservice.entity.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImages, Long> {
}
