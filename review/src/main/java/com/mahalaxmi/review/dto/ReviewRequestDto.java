package com.mahalaxmi.review.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {

    private Long productVariantId;
    private Integer rating;
    private String comment;
}