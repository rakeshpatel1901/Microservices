package com.learning.productservice.dto.Admin;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Builder
public class AdminProductSummaryDTO {

    private Long    productId;
    private String  name;
    private String  description;
    private String  categoryName;
    private Long    categoryId;


    private Long       primaryVariantId;
    private String     skuCode;
    private String     brand;
    private BigDecimal sellingPrice;
    private BigDecimal mrp;
    private Integer    stock;
    private Boolean    inStock;
    private String     imageUrl;

    private Integer    variantCount;
}
