package com.learning.productservice.dto.Admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminProductStatsDTO {
    private long totalProducts;
    private long inStock;         // products where at least one variant has stock > 0
    private long outOfStock;      // products where ALL variants have stock == 0
    private long lowStock;        // products where total stock across variants is 1–3
}

