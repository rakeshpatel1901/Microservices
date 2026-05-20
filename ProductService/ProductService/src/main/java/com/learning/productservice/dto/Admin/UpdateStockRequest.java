package com.learning.productservice.dto.Admin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStockRequest {

    @NotNull
    @Min(0)
    private Integer stock;
}

