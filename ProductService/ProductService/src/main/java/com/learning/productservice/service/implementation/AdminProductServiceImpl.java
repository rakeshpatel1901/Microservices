package com.learning.productservice.service.implementation;

// ─────────────────────────────────────────────────────────────────────────────
// FILE: product-service/.../service/AdminProductService.java
// ─────────────────────────────────────────────────────────────────────────────



import com.learning.productservice.dto.Admin.*;
import com.learning.productservice.entity.*;
import com.learning.productservice.repository.*;
import com.learning.productservice.service.AdminProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductRepository        productRepository;
    private final ProductVariantRepository variantRepository;
    private final CategoryRepository     categoriesRepository;

    // ── Stat cards ────────────────────────────────────────────────────────────
    public AdminProductStatsDTO getStats() {
        return AdminProductStatsDTO.builder()
                .totalProducts(productRepository.count())
                .inStock(productRepository.countInStock())
                .outOfStock(productRepository.countOutOfStock())
                .lowStock(productRepository.countLowStock())
                .build();
    }

    // ── Paginated list ────────────────────────────────────────────────────────
    public Page<AdminProductSummaryDTO> getProducts(
            Long categoryId, String search, int page, int size) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 20),
                Sort.by(Sort.Direction.DESC, "productId"));

        Page<Product> productPage = productRepository.findAllWithFilters(
                categoryId, (search == null || search.isBlank()) ? null : search.trim(),
                pageable);

        return productPage.map(this::toSummary);
    }

    // ── Single product detail (for edit modal) ────────────────────────────────
    public AdminProductDetailDTO getProductDetail(Long productId) {
        Product p = findProduct(productId);
        return toDetail(p);
    }

    // ── Create product ────────────────────────────────────────────────────────
    @Transactional
    public AdminProductDetailDTO createProduct(CreateProductRequest req) {
        Categories category = findCategory(req.getCategoryId());

        // Validate SKU uniqueness
        req.getVariants().forEach(v -> {
            if (variantRepository.existsBySkuCode(v.getSkuCode())) {
                throw new IllegalArgumentException("SKU already exists: " + v.getSkuCode());
            }
        });

        Product product = new Product();
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setCategory(category);

        List<ProductVariant> variants = buildVariants(req.getVariants(), product);
        product.setProductVariants(variants);

        return toDetail(productRepository.save(product));
    }

    // ── Update product ────────────────────────────────────────────────────────
    @Transactional
    public AdminProductDetailDTO updateProduct(Long productId, UpdateProductRequest req) {
        Product product  = findProduct(productId);
        Categories category = findCategory(req.getCategoryId());

        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setCategory(category);

        // Validate SKU uniqueness (excluding own variants)
        req.getVariants().forEach(v -> {
            // Only check for SKU collision on NEW variants (no id yet)
            if (variantRepository.existsBySkuCode(v.getSkuCode())) {
                // Allow if the SKU belongs to one of this product's own existing variants
                boolean ownSku = product.getProductVariants().stream()
                        .anyMatch(ev -> ev.getSkuCode().equals(v.getSkuCode()));
                if (!ownSku) {
                    throw new IllegalArgumentException("SKU already exists: " + v.getSkuCode());
                }
            }
        });

        // Replace variants (orphanRemoval on the @OneToMany handles deletes)
        product.getProductVariants().clear();
        List<ProductVariant> newVariants = buildVariants(req.getVariants(), product);
        product.getProductVariants().addAll(newVariants);

        return toDetail(productRepository.save(product));
    }

    // ── Delete product ────────────────────────────────────────────────────────
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = findProduct(productId);
        productRepository.delete(product);
    }

    // ── Update stock on a specific variant ────────────────────────────────────
    @Transactional
    public AdminProductSummaryDTO updateVariantStock(Long variantId, int newStock) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new EntityNotFoundException("Variant not found: " + variantId));
        variant.setStock(newStock);
        variantRepository.save(variant);
        return toSummary(variant.getProduct());
    }

    // ── Toggle in-stock (set all variants to 0 or restore to 1 if 0) ─────────
    @Transactional
    public AdminProductSummaryDTO toggleInStock(Long productId) {
        Product product = findProduct(productId);
        boolean currentlyInStock = product.getProductVariants().stream()
                .anyMatch(v -> v.getStock() != null && v.getStock() > 0);

        if (currentlyInStock) {
            // Mark all variants as 0 (out of stock)
            product.getProductVariants().forEach(v -> v.setStock(0));
        } else {
            // Restore each variant to at least 1 if it was 0
            product.getProductVariants().forEach(v -> {
                if (v.getStock() == null || v.getStock() == 0) v.setStock(1);
            });
        }
        productRepository.save(product);
        return toSummary(product);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mappers
    // ─────────────────────────────────────────────────────────────────────────

    private AdminProductSummaryDTO toSummary(Product p) {
        List<ProductVariant> variants = p.getProductVariants() == null
                ? List.of() : p.getProductVariants();

        // Pick the first variant as the "primary" for table display
        ProductVariant primary = variants.isEmpty() ? null : variants.get(0);

        // Aggregate stock across all variants
        int totalStock = variants.stream()
                .mapToInt(v -> v.getStock() == null ? 0 : v.getStock())
                .sum();

        // Primary image from the primary variant
        String imageUrl = null;
        if (primary != null && primary.getProductImages() != null) {
            imageUrl = primary.getProductImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                    .map(ProductImages::getImageUrl)
                    .findFirst()
                    .orElse(primary.getProductImages().isEmpty()
                            ? null : primary.getProductImages().get(0).getImageUrl());
        }

        return AdminProductSummaryDTO.builder()
                .productId(p.getProductId())
                .name(p.getName())
                .description(p.getDescription())
                .categoryId(p.getCategory() != null ? p.getCategory().getCategoryId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getCategoryName() : null)
                .primaryVariantId(primary != null ? primary.getProductVariantId() : null)
                .skuCode(primary != null ? primary.getSkuCode() : null)
                .brand(primary != null ? primary.getBrand() : null)
                .sellingPrice(primary != null ? primary.getSellingPrice() : null)
                .mrp(primary != null ? primary.getMrp() : null)
                .stock(totalStock)
                .inStock(totalStock > 0)
                .imageUrl(imageUrl)
                .variantCount(variants.size())
                .build();
    }

    private AdminProductDetailDTO toDetail(Product p) {
        List<AdminProductDetailDTO.AdminVariantDTO> variantDTOs =
                (p.getProductVariants() == null ? List.<ProductVariant>of() : p.getProductVariants())
                        .stream().map(v -> {
                            List<AdminProductDetailDTO.AdminImageDTO> imageDTOs =
                                    (v.getProductImages() == null ? List.<ProductImages>of() : v.getProductImages())
                                            .stream().map(img -> AdminProductDetailDTO.AdminImageDTO.builder()
                                                    .productImagesId(img.getProductImagesId())
                                                    .imageUrl(img.getImageUrl())
                                                    .isPrimary(img.getIsPrimary())
                                                    .build())
                                            .collect(Collectors.toList());

                            return AdminProductDetailDTO.AdminVariantDTO.builder()
                                    .productVariantId(v.getProductVariantId())
                                    .color(v.getColor())
                                    .size(v.getSize())
                                    .brand(v.getBrand())
                                    .mrp(v.getMrp())
                                    .sellingPrice(v.getSellingPrice())
                                    .buyingPrice(v.getBuyingPrice())
                                    .stock(v.getStock())
                                    .skuCode(v.getSkuCode())
                                    .images(imageDTOs)
                                    .build();
                        }).collect(Collectors.toList());

        return AdminProductDetailDTO.builder()
                .productId(p.getProductId())
                .name(p.getName())
                .description(p.getDescription())
                .categoryId(p.getCategory() != null ? p.getCategory().getCategoryId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getCategoryName() : null)
                .variants(variantDTOs)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private List<ProductVariant> buildVariants(
            List<CreateProductRequest.VariantRequest> variantReqs, Product product) {

        return variantReqs.stream().map(vr -> {
            ProductVariant v = new ProductVariant();
            v.setProduct(product);
            v.setSkuCode(vr.getSkuCode());
            v.setColor(vr.getColor());
            v.setSize(vr.getSize());
            v.setBrand(vr.getBrand());
            v.setMrp(vr.getMrp());
            v.setSellingPrice(vr.getSellingPrice());
            v.setBuyingPrice(vr.getBuyingPrice());
            v.setStock(vr.getStock());

            List<ProductImages> images = new ArrayList<>();
            if (vr.getImages() != null) {
                for (CreateProductRequest.ImageRequest ir : vr.getImages()) {
                    ProductImages img = new ProductImages();
                    img.setImageUrl(ir.getImageUrl());
                    img.setIsPrimary(Boolean.TRUE.equals(ir.getIsPrimary()));
                    img.setProductVariant(v);
                    images.add(img);
                }
            }
            v.setProductImages(images);
            return v;
        }).collect(Collectors.toList());
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    private Categories findCategory(Long id) {
        return categoriesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
    }
}
