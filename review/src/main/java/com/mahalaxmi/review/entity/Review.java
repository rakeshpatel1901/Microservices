package com.mahalaxmi.review.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productVariantId;

    private Long userId;

    private Integer rating;

    private String comment;

    private Boolean verifiedPurchase;

    private LocalDateTime createdAt;
}