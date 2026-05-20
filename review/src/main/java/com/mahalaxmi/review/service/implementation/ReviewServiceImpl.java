package com.mahalaxmi.review.service.implementation;

import com.mahalaxmi.review.dto.ReviewRequestDto;
import com.mahalaxmi.review.dto.ReviewResponseDto;
import com.mahalaxmi.review.dto.ReviewSummaryDto;
import com.mahalaxmi.review.entity.Review;
import com.mahalaxmi.review.externalservices.AuthClient;
import com.mahalaxmi.review.externalservices.OrderClient;
import com.mahalaxmi.review.repository.ReviewRepository;
import com.mahalaxmi.review.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final AuthClient authClient;
    private final OrderClient orderClient;
    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             AuthClient authClient,
                             OrderClient orderClient) {
        this.reviewRepository = reviewRepository;
        this.authClient = authClient;
        this.orderClient = orderClient;
    }

    private Long getUserId() {
        return authClient.getUserId();
    }

    @Override
    public String addReview(ReviewRequestDto dto) {

        Long userId = getUserId();


        Optional<Review> existing =
                reviewRepository.findByProductVariantIdAndUserId(
                        dto.getProductVariantId(), userId);

        if (existing.isPresent()) {
            throw new RuntimeException("You already reviewed this product");
        }
        boolean isPurchased = orderClient.hasUserPurchasedProduct(
                dto.getProductVariantId()
        );

        Review review = new Review();
        review.setProductVariantId(dto.getProductVariantId());
        review.setUserId(userId);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setVerifiedPurchase(isPurchased);
        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);

        return "Review added";
    }

    @Override
    public List<ReviewResponseDto> getReviewsByProduct(Long productVariantId) {

        return reviewRepository.findByProductVariantId(productVariantId)
                .stream()
                .map(r -> {
                    ReviewResponseDto dto = new ReviewResponseDto();
                    dto.setId(r.getId());
                    dto.setProductVariantId(r.getProductVariantId());
                    dto.setUserId(r.getUserId());
                    dto.setRating(r.getRating());
                    dto.setComment(r.getComment());
                    return dto;
                })
                .toList();
    }

    @Override
    public String deleteReview(Long reviewId) {

        Long userId = getUserId();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        reviewRepository.delete(review);


        return "Review deleted";
    }

    public ReviewSummaryDto getReviewSummary(Long productId) {

        Double avg = reviewRepository.getAverageRating(productId);
        Long count = reviewRepository.getTotalReviews(productId);

        ReviewSummaryDto dto = new ReviewSummaryDto();
        dto.setAverageRating(avg != null ? avg : 0.0);
        dto.setTotalReviews(count);

        return dto;
    }


    public Page<ReviewResponseDto> getReviewsByProduct(
            Long productId,
            int page,
            int size,
            String sortBy) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, sortBy)
        );

        Page<Review> reviewPage =
                reviewRepository.findByProductVariantId(productId, pageable);

        return reviewPage.map(r -> {
            ReviewResponseDto dto = new ReviewResponseDto();
            dto.setId(r.getId());
            dto.setProductVariantId(r.getProductVariantId());
            dto.setUserId(r.getUserId());
            dto.setRating(r.getRating());
            dto.setComment(r.getComment());
            return dto;
        });
    }
}
