package com.learning.productservice.dto.Admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateProductRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Long categoryId;

    // Variants submitted with a non-null productVariantId are updated.
    // Variants with productVariantId == null are treated as NEW variants.
    // Existing variants NOT present in this list are deleted (orphan removal handles it).
    @NotNull
    private List<CreateProductRequest.VariantRequest> variants;
}


