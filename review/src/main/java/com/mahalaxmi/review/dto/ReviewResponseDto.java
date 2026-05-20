package com.mahalaxmi.review.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewResponseDto {

    private Long id;
    private Long productVariantId;
    private Long userId;
    private Integer rating;
    private String comment;
}