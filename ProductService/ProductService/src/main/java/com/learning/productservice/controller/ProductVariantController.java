package com.learning.productservice.controller;


import com.learning.productservice.dto.ApiResponseDto;
import com.learning.productservice.dto.ProductVariantOrderResponseDto;
import com.learning.productservice.service.ProductVariantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
