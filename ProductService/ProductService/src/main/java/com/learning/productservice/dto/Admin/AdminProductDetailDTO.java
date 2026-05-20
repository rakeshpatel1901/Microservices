package com.learning.productservice.dto.Admin;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AdminProductDetailDTO {

    private Long    productId;
    private String  name;
    private String  description;
    private Long    categoryId;
    private String  categoryName;

    private List<AdminVariantDTO> variants;

    @Data
    @Builder
    public static class AdminVariantDTO {
        private Long       productVariantId;
        private String     color;
        private String     size;
        private String     brand;
        private BigDecimal mrp;
        private BigDecimal sellingPrice;
        private BigDecimal buyingPrice;
        private Integer    stock;
        private String     skuCode;
        private List<AdminImageDTO> images;
    }

    @Data
    @Builder
    public static class AdminImageDTO {
        private Long    productImagesId;
        private String  imageUrl;
        private Boolean isPrimary;
    }
}

