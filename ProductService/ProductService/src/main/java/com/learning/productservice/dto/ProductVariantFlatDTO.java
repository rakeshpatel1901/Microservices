package com.learning.productservice.dto;

import java.math.BigDecimal;

public interface ProductVariantFlatDTO {

    Long getProductVariantId();
    String getColor();
    String getSize();
    String getBrand();

    BigDecimal getMrp();
    BigDecimal getSellingPrice();
    BigDecimal getBuyingPrice();

    Integer getStock();
    String getSkuCode();

    Long getProductImagesId();
    String getImageUrl();
    Boolean getIsPrimary();
}