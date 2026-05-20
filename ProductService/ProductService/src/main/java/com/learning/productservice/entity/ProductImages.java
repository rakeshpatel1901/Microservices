package com.learning.productservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long productImagesId;

    @Column(nullable = false)
    String imageUrl;

    @Column(nullable = false)
    Boolean isPrimary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id")
    ProductVariant productVariant;
}


