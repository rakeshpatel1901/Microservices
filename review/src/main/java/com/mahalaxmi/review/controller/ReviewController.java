package com.mahalaxmi.review.controller;

import com.mahalaxmi.review.dto.ReviewRequestDto;
import com.mahalaxmi.review.dto.ReviewResponseDto;
import com.mahalaxmi.review.dto.ReviewSummaryDto;
import com.mahalaxmi.review.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<String> addReview(@RequestBody ReviewRequestDto dto) {
        return ResponseEntity.ok(reviewService.addReview(dto));
    }

    @GetMapping("/product/{productVariantId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviews(
            @PathVariable Long productVariantId) {

        return ResponseEntity.ok(
                reviewService.getReviewsByProduct(productVariantId));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.deleteReview(reviewId));
    }

    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<ReviewSummaryDto> getSummary(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewSummary(productId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResponseDto>> getReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        return ResponseEntity.ok(
                reviewService.getReviewsByProduct(productId, page, size, sortBy)
        );
    }
}