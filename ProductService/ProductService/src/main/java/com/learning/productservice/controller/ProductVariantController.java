package com.learning.productservice.controller;


import com.learning.productservice.dto.*;
import com.learning.productservice.service.ProductVariantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/productVariant")
@Slf4j
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService){
        this.productVariantService = productVariantService;
    }

    @GetMapping("/{productVariantId}/{quantity}")
    public ApiResponseDto<ProductVariantOrderResponseDto> reserveStockProductVariantById(
            @PathVariable Long productVariantId,
            @PathVariable Integer quantity){
        log.info("Inside the Product Variant Reserving the Stock");
        System.out.println("Inside Variant");

        ProductVariantOrderResponseDto stock = productVariantService.reserveStock(productVariantId,quantity);

        if(!stock.isAvailable()){
            throw new IllegalArgumentException("Quantity Out of Stock");
        }

        ApiResponseDto<ProductVariantOrderResponseDto> response = new ApiResponseDto<>();
        response.setStatus("SUCCESS");
        response.setStatusCode(200);
        response.setMessage("Quantity Available");
        response.setData(stock);
        return response;
    }

    @GetMapping("/unreserve/{productVariantId}/{quantity}")
    public ApiResponseDto<ProductVariantOrderResponseDto> unreserveStockProductVariantById(
            @PathVariable Long productVariantId,
            @PathVariable Integer quantity){
        log.info("Inside the Product Variant UnReserving the Stock");
        System.out.println("Inside Variant");

        ProductVariantOrderResponseDto stock = productVariantService.unreserveStock(productVariantId,quantity);
        ApiResponseDto<ProductVariantOrderResponseDto> response = new ApiResponseDto<>();
        response.setStatus("SUCCESS");
        response.setStatusCode(200);
        response.setMessage("Quantity Updated");
        response.setData(stock);
        return response;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductVariantResponseDTO>> getVariantsByProductId(
            @PathVariable Long productId
    ) {
        List<ProductVariantResponseDTO> variants =
                productVariantService.getVariantsByProductId(productId);

        return ResponseEntity.ok(variants);
    }

    @PostMapping("/bulk")
    public List<ProductVariantCartResponseDto> getVariants(@RequestBody List<Long> ids) {
        return productVariantService.getVariantsByIds(ids);
    }

    @GetMapping("/getVariant/{id}")
    public ResponseEntity<ProductVariantUserOrderResponseDto> getVariant(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productVariantService.getVariantById(id)
        );
    }

    @GetMapping("/variants/batch")
    List<ProductVariantUserOrderResponseDto> getOrderVariantsByIds(@RequestParam("ids") List<Long> variantIds){
        return productVariantService.getOrderVariantsByIds(variantIds);
    }

}
