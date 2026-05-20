package com.learning.productservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long categoryId;

    @Column(unique = true, nullable = false)
    String categoryName;

    String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL , orphanRemoval = true, fetch = FetchType.LAZY)
    List<Product> products;
}
