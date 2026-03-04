package com.learning.productservice.controller;


import com.learning.productservice.dto.ApiResponseDto;
import com.learning.productservice.dto.ProductCardResponseDTO;
import com.learning.productservice.dto.ProductDetailDTO;
import com.learning.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    ProductService productService;


    @GetMapping
    public ResponseEntity<ApiResponseDto<List<ProductCardResponseDTO>>> getAllProducts(
            @RequestParam(defaultValue = "1" ) int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword
    ) {

        List<String> allowedSort = new ArrayList<>(List.of("mrp","sellingPrice","name","productId"));
        if(allowedSort.contains(sortBy)){
            List<ProductCardResponseDTO> productCardResponseDTOS =
                    productService.getAllProducts(categoryId,keyword,  PageRequest.of(pageNo-1, pageSize),sortBy,order);
            ApiResponseDto<List<ProductCardResponseDTO>> response = new ApiResponseDto<>();
            response.setStatus("SUCCESS");
            response.setStatusCode(200);
            response.setData(productCardResponseDTOS);
            response.setMessage("Products fetched successfully");


            return ResponseEntity.ok(response);
        }
        throw new IllegalArgumentException("Cannot Sort Value By "+sortBy);

    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailDTO> getProductDetails(@PathVariable Long productId){
        ProductDetailDTO productDetailDTO = productService.getProductDetails(productId);
        return ResponseEntity.ok(productDetailDTO);
    }



}
