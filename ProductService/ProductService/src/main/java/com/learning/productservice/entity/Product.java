package com.learning.productservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name="products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long productId;

    String name;

    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Categories category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL , orphanRemoval = true , fetch = FetchType.LAZY)
    List<ProductVariant> productVariants;

//    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    List<Wishlist> wishlistList;
//
//    @OneToMany(mappedBy = "product" , cascade=CascadeType.ALL , orphanRemoval = true, fetch = FetchType.LAZY)
//    List<Reviews> reviews;
}
