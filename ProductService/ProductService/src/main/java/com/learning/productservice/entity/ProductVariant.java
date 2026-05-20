package com.learning.productservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long productVariantId;

    String color;
    String size;
    String brand;
    BigDecimal mrp;
    BigDecimal sellingPrice;
    BigDecimal buyingPrice;
    Integer stock;

    @Column(unique = true, nullable = false)
    String skuCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    Product product;

    @OneToMany(mappedBy = "productVariant" , cascade = CascadeType.ALL , orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 10)
    List<ProductImages> productImages;
}
