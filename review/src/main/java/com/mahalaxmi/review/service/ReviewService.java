package com.mahalaxmi.review.service;

import com.mahalaxmi.review.dto.ReviewRequestDto;
import com.mahalaxmi.review.dto.ReviewResponseDto;
import com.mahalaxmi.review.dto.ReviewSummaryDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReviewService {

    String addReview(ReviewRequestDto dto);

    List<ReviewResponseDto> getReviewsByProduct(Long productVariantId);
    ReviewSummaryDto getReviewSummary(Long productId);
    String deleteReview(Long reviewId);
    Page<ReviewResponseDto> getReviewsByProduct(Long productId, int page, int size, String sortBy);
}