package com.mahalaxmi.review.repository;

import com.mahalaxmi.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductVariantId(Long productVariantId);

    Optional<Review> findByProductVariantIdAndUserId(Long productVariantId, Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productVariantId = :productId")
    Double getAverageRating(Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.productVariantId = :productId")
    Long getTotalReviews(Long productId);

    Page<Review> findByProductVariantId(Long productId, Pageable pageable);
}