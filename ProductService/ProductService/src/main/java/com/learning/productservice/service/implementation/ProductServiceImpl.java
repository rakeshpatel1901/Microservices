package com.learning.productservice.service.implementation;

import com.learning.productservice.dto.*;
import com.learning.productservice.entity.Categories;
import com.learning.productservice.entity.Product;
import com.learning.productservice.entity.ProductImages;
import com.learning.productservice.entity.ProductVariant;
import com.learning.productservice.mapper.CategoriesMapper;
import com.learning.productservice.mapper.ProductImagesMapper;
import com.learning.productservice.mapper.ProductMapper;
import com.learning.productservice.mapper.ProductVariantMapper;
import com.learning.productservice.repository.CategoryRepository;
import com.learning.productservice.repository.ProductImageRepository;
import com.learning.productservice.repository.ProductRepository;
import com.learning.productservice.repository.ProductVariantRepository;
import com.learning.productservice.service.ProductService;
import com.learning.productservice.service.S3Service;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private CategoriesMapper categoriesMapper;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ProductVariantMapper productVariantMapper;

    @Autowired
    ProductImagesMapper productImagesMapper;


    @Autowired
    ProductVariantRepository productVariantRepository;

    @Autowired
    ProductImageRepository productImageRepository;


    private final S3Service s3Service;

    public ProductServiceImpl(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @Override
    public List<ProductCardResponseDTO> getAllProducts(Long categoryId, String keyword, Pageable pageable, String sortBy, String order) {
        return productRepository.findProductCards(categoryId, keyword, pageable).getContent();
    }

    @Override
    public ProductDetailDTO getProductDetails(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("No Such Product Exists"));
        return productMapper.toProductDetailDTO(product);
    }


    // Admin Add Product Method
    @Transactional
    @Override
    public String addProduct(AddProductRequestDTO dto) {

        Categories category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setCategory(category);

        if (dto.getProductVariants() != null && !dto.getProductVariants().isEmpty()) {

            List<ProductVariant> variants = dto.getProductVariants().stream().map(variantDTO -> {

                ProductVariant variant = productVariantMapper.toProductVariant(variantDTO);
                variant.setProduct(product);

                if (variantDTO.getProductImagesList() != null) {
                    List<ProductImages> images = variantDTO.getProductImagesList().stream().map(imageDTO -> {

                        String imageUrl = s3Service.uploadFile(imageDTO.getImageUrl());

                        ProductImages image = new ProductImages();
                        image.setImageUrl(imageUrl);
                        image.setIsPrimary(imageDTO.getIsPrimary());
                        image.setProductVariant(variant);

                        return image;

                    }).toList();
                    variant.setProductImages(images);
                }

                return variant;

            }).toList();
            product.setProductVariants(variants);
        }

        productRepository.save(product);

        return "Product Created Successfully";
    }

    @Transactional
    @Override
    public String addProductVariant(ProductVariantRequestDTO dto, Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        ProductVariant variant = productVariantMapper.toProductVariant(dto);
        variant.setProduct(product);

        if (dto.getProductImagesList() != null && !dto.getProductImagesList().isEmpty()) {

            // ✅ Validate only one primary image
            long primaryCount = dto.getProductImagesList().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                    .count();

            if (primaryCount > 1) {
                throw new IllegalArgumentException("Only one primary image allowed");
            }

            // ✅ Upload images to S3
            List<ProductImages> images = dto.getProductImagesList().stream()
                    .map(imageDTO -> {

                        String imageUrl = null;

                        // 🔥 Upload file to S3
                        if (imageDTO.getImageUrl() != null && !imageDTO.getImageUrl().isEmpty()) {
                            imageUrl = s3Service.uploadFile(imageDTO.getImageUrl());
                        } else {
                            throw new IllegalArgumentException("Image file is required");
                        }

                        ProductImages image = new ProductImages();
                        image.setImageUrl(imageUrl);
                        image.setIsPrimary(Boolean.TRUE.equals(imageDTO.getIsPrimary()));
                        image.setProductVariant(variant);

                        return image;

                    }).toList();

            variant.setProductImages(images);
        }

        productVariantRepository.save(variant);

        return "Product Variant Added Successfully";
    }

    @Transactional
    @Override
    public String addProductVariantImage(ProductImageRequestDTO dto, Long productVariantId) {

        ProductVariant variant = productVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new IllegalArgumentException("Product Variant not found"));

        // 🔥 Validate file
        if (dto.getImageUrl() == null || dto.getImageUrl().isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }

        // 🔥 Upload to S3
        String imageUrl = s3Service.uploadFile(dto.getImageUrl());

        // 🔥 If new image is primary → unset others
        if (Boolean.TRUE.equals(dto.getIsPrimary())) {

            List<ProductImages> existingImages = variant.getProductImages();

            if (existingImages != null) {
                existingImages.forEach(img -> img.setIsPrimary(false));
            }
        }

        // 🔥 Create new image entity
        ProductImages image = new ProductImages();
        image.setImageUrl(imageUrl);
        image.setIsPrimary(Boolean.TRUE.equals(dto.getIsPrimary()));
        image.setProductVariant(variant);

        // 🔥 Add to variant
        if (variant.getProductImages() == null) {
            variant.setProductImages(new ArrayList<>());
        }

        variant.getProductImages().add(image);

        productVariantRepository.save(variant);

        return "Product Variant Image Added Successfully";
    }

    @Transactional
    @Override
    public String updateProduct(Long productId, AddProductRequestDTO dto) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());

        if (dto.getCategoryId() != null) {
            Categories category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            product.setCategory(category);
        }

        return "Product Updated Successfully";
    }

    @Transactional
    @Override
    public String deleteProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        productRepository.delete(product);

        return "Product Deleted Successfully";
    }

    @Transactional
    @Override
    public String updateProductVariant(Long variantId, ProductVariantRequestDTO dto) {

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found"));

        variant.setColor(dto.getColor());
        variant.setSize(dto.getSize());
        variant.setBrand(dto.getBrand());
        variant.setMrp(dto.getMrp());
        variant.setSellingPrice(dto.getSellingPrice());
        variant.setBuyingPrice(dto.getBuyingPrice());
        variant.setStock(dto.getStock());

        return "Product Variant Updated Successfully";
    }

    @Transactional
    @Override
    public String deleteProductVariant(Long variantId) {

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new IllegalArgumentException("Variant not found"));

        productVariantRepository.delete(variant);

        return "Product Variant Deleted Successfully";
    }

    @Transactional
    @Override
    public String updateProductVariantImage(Long imageId, ProductImageRequestDTO dto) {

        ProductImages image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
            String imageUrl = s3Service.uploadFile(dto.getImageUrl());
            image.setImageUrl(imageUrl);
        }


        if (Boolean.TRUE.equals(dto.getIsPrimary())) {

            ProductVariant variant = image.getProductVariant();

            variant.getProductImages().forEach(img -> img.setIsPrimary(false));

            image.setIsPrimary(true);
        } else {
            image.setIsPrimary(false);
        }

        return "Product Image Updated Successfully";
    }


    @Transactional
    @Override
    public String deleteProductVariantImage(Long imageId) {

        ProductImages image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        ProductVariant variant = image.getProductVariant();


        if (image.getImageUrl() != null) {
            s3Service.deleteFile(image.getImageUrl());
        }


        if (variant.getProductImages() != null) {
            variant.getProductImages().remove(image);
        }

        productImageRepository.delete(image);


        if (Boolean.TRUE.equals(image.getIsPrimary()) && variant.getProductImages() != null) {

            variant.getProductImages().stream()
                    .findFirst()
                    .ifPresent(img -> img.setIsPrimary(true)); // assign new primary
        }

        return "Product Image Deleted Successfully";
    }


    @Override
    public List<ProductCardResponseDTO> getProductsByCategory(Long categoryId, int limit) {

        List<ProductCardResponseDTO> products = productRepository.findByCategory_CategoryId(categoryId);

        return products.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }


}
