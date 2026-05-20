package com.learning.productservice.dto.Admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Long categoryId;

    @NotNull
    private List<VariantRequest> variants;

    @Data
    public static class VariantRequest {

        @NotBlank
        private String skuCode;

        private String     color;
        private String     size;

        @NotBlank
        private String     brand;

        @NotNull
        private BigDecimal mrp;

        @NotNull
        private BigDecimal sellingPrice;

        @NotNull
        private BigDecimal buyingPrice;

        @NotNull
        private Integer    stock;

        private List<ImageRequest> images;
    }

    @Data
    public static class ImageRequest {
        private String  imageUrl;
        private Boolean isPrimary;
    }
}

