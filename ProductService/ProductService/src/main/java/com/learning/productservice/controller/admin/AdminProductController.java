package com.learning.productservice.controller.admin;


import com.learning.productservice.dto.AddProductRequestDTO;
import com.learning.productservice.dto.Admin.*;
import com.learning.productservice.dto.ApiResponseDto;
import com.learning.productservice.dto.ProductImageRequestDTO;
import com.learning.productservice.dto.ProductVariantRequestDTO;
import com.learning.productservice.service.AdminProductService;
import com.learning.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product/v1/admin")
public class AdminProductController {

    ProductService productService;
    private final AdminProductService adminProductService;


    public AdminProductController(ProductService productService, AdminProductService adminProductService){
        this.productService = productService;
        this.adminProductService = adminProductService;
    }

    @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE )
    public ResponseEntity<ApiResponseDto<String>> addProduct(@Valid @ModelAttribute  AddProductRequestDTO productRequestDTO){

        String message = productService.addProduct(productRequestDTO);
        ApiResponseDto<String> apiResponseDTO = new ApiResponseDto<>();
        apiResponseDTO.setStatus("SUCCESS");
        apiResponseDTO.setMessage(message);

        return ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
    }

    @PostMapping("/addProductVariant/{productId}")
    public ResponseEntity<ApiResponseDto<String>> addProductVariant(@Valid @ModelAttribute ProductVariantRequestDTO productVariantRequestDTO,
                                                                    @PathVariable Long productId){
        String message = productService.addProductVariant(productVariantRequestDTO, productId);
        ApiResponseDto<String> apiResponseDTO = new ApiResponseDto<>();
        apiResponseDTO.setMessage(message);
        apiResponseDTO.setStatus("SUCCESS");
        return  ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
    }

        @PostMapping("/addProductImage/{productVariantId}")
    public ResponseEntity<ApiResponseDto<String>> addProductImage(@Valid @ModelAttribute ProductImageRequestDTO productImageRequestDTO,
                                                                  @PathVariable Long productVariantId){


        String message = productService.addProductVariantImage(productImageRequestDTO, productVariantId);

        ApiResponseDto<String> apiResponseDTO = new ApiResponseDto<>();
        apiResponseDTO.setMessage(message);
        apiResponseDTO.setStatus("SUCCESS");
        return  ResponseEntity.status(HttpStatus.OK).body(apiResponseDTO);
    }



    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/admin/products/stats
    // Stat card counts — call this once on tab mount
    // Returns: { totalProducts, inStock, outOfStock, lowStock }
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/stats")
    public ResponseEntity<AdminProductStatsDTO> getStats() {
        return ResponseEntity.ok(adminProductService.getStats());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/admin/products?page=0&size=10&categoryId=2&search=samsung
    // Paginated product list for the table
    // Returns Spring Page<AdminProductSummaryDTO>:
    //   { content:[...], totalElements, totalPages, number, size }
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<Page<AdminProductSummaryDTO>> getProducts(
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "10") int    size,
            @RequestParam(required = false)    Long   categoryId,
            @RequestParam(required = false)    String search
    ) {
        return ResponseEntity.ok(
                adminProductService.getProducts(categoryId, search, page, size));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/admin/products/{productId}
    // Full product detail (all variants + images) — load into the Edit modal
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/{productId}")
    public ResponseEntity<AdminProductDetailDTO> getProductDetail(
            @PathVariable Long productId) {
        return ResponseEntity.ok(adminProductService.getProductDetail(productId));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/admin/products
    // Create a new product with one or more variants
    // Body: CreateProductRequest
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<AdminProductDetailDTO> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminProductService.createProduct(request));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/admin/products/{productId}
    // Replace product info + variants (full update)
    // Body: UpdateProductRequest
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/{productId}")
    public ResponseEntity<AdminProductDetailDTO> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(adminProductService.updateProduct(productId, request));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/admin/products/{productId}
    // Permanently delete product + all variants + images (cascade)
    // ─────────────────────────────────────────────────────────────────────────
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        adminProductService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PATCH /api/admin/products/variants/{variantId}/stock
    // Update stock quantity of a specific variant
    // Body: { "stock": 25 }
    // Returns updated parent product summary
    // ─────────────────────────────────────────────────────────────────────────
    @PatchMapping("/variants/{variantId}/stock")
    public ResponseEntity<AdminProductSummaryDTO> updateStock(
            @PathVariable Long variantId,
            @Valid @RequestBody UpdateStockRequest request) {
        return ResponseEntity.ok(
                adminProductService.updateVariantStock(variantId, request.getStock()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PATCH /api/admin/products/{productId}/toggle-stock
    // Toggle all variants between in-stock / out-of-stock
    // Returns updated product summary
    // ─────────────────────────────────────────────────────────────────────────
    @PatchMapping("/{productId}/toggle-stock")
    public ResponseEntity<AdminProductSummaryDTO> toggleStock(
            @PathVariable Long productId) {
        return ResponseEntity.ok(adminProductService.toggleInStock(productId));
    }
}
