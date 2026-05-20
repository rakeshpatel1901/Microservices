package com.learning.productservice.service.implementation;

import com.learning.productservice.dto.*;
import com.learning.productservice.entity.Product;
import com.learning.productservice.entity.ProductVariant;
import com.learning.productservice.mapper.ProductVariantMapper;
import com.learning.productservice.repository.ProductVariantRepository;
import com.learning.productservice.service.ProductVariantService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantMapper productVariantMapper;
    private final ProductVariantRepository productVariantRepository;

    public ProductVariantServiceImpl(ProductVariantRepository productVariantRepository,
                                     ProductVariantMapper productVariantMapper){
        this.productVariantRepository = productVariantRepository;
        this.productVariantMapper = productVariantMapper;
    }

    @Transactional
    @Override
    public ProductVariantOrderResponseDto reserveStock(Long productVariantId, Integer quantity) {

        ProductVariant productVariant = productVariantRepository.findById(productVariantId).
                orElseThrow(() -> new IllegalArgumentException("Product is not found"));

        int quant = productVariant.getStock();
        ProductVariantOrderResponseDto productVariantOrderResponseDto;
        if(quant - quantity >= 0){
             productVariant.setStock(quant-quantity);
             productVariantOrderResponseDto =
                    productVariantMapper.toProductVariantOrderResponseDto(productVariant);
             productVariantOrderResponseDto.setAvailable(true);
        }
        else{
            productVariantOrderResponseDto =
                    productVariantMapper.toProductVariantOrderResponseDto(productVariant);
            productVariantOrderResponseDto.setAvailable(false);
        }

        return productVariantOrderResponseDto;
    }

    @Override
    public ProductVariantOrderResponseDto unreserveStock(Long productVariantId, Integer quantity) {
        ProductVariant productVariant = productVariantRepository.findById(productVariantId).
                orElseThrow(() -> new IllegalArgumentException("Product is not found"));

        int quant = productVariant.getStock();
        productVariant.setStock(quant+quantity);
        ProductVariantOrderResponseDto productVariantOrderResponseDto =productVariantMapper.toProductVariantOrderResponseDto(productVariant);
        productVariantOrderResponseDto.setAvailable(true);

        return productVariantOrderResponseDto;
    }

    @Override
    public List<ProductVariantResponseDTO> getVariantsByProductId(Long productId) {

        List<ProductVariantFlatDTO> flatList =
                productVariantRepository.findVariantsFlatByProductId(productId);

        Map<Long, ProductVariantResponseDTO> map = new LinkedHashMap<>();

        for (ProductVariantFlatDTO row : flatList) {

            ProductVariantResponseDTO variant = map.computeIfAbsent(
                    row.getProductVariantId(),
                    id -> {
                        ProductVariantResponseDTO dto = new ProductVariantResponseDTO();

                        dto.setProductVariantId(id);
                        dto.setColor(row.getColor());
                        dto.setSize(row.getSize());
                        dto.setBrand(row.getBrand());

                        dto.setMrp(row.getMrp());
                        dto.setSellingPrice(row.getSellingPrice());


                        dto.setStock(row.getStock());
                        dto.setSkuCode(row.getSkuCode());

                        dto.setProductImagesList(new ArrayList<>());
                        return dto;
                    }
            );

            if (row.getProductImagesId() != null) {
                ProductImagesResponseDTO image = new ProductImagesResponseDTO();
                image.setProductImagesId(row.getProductImagesId());
                image.setImageUrl(row.getImageUrl());
                image.setIsPrimary(row.getIsPrimary());

                variant.getProductImagesList().add(image);
            }
        }

        return new ArrayList<>(map.values());
    }

    @Override
    public List<ProductVariantCartResponseDto> getVariantsByIds(List<Long> ids) {

        List<ProductVariant> variants = productVariantRepository.findAllById(ids);

        return variants.stream().map(v -> {
            ProductVariantCartResponseDto dto = new ProductVariantCartResponseDto();

            dto.setProductVariantId(v.getProductVariantId());
            dto.setProductName(v.getProduct().getName());
            dto.setColor(v.getColor());
            dto.setSize(v.getSize());
            dto.setSellingPrice(v.getSellingPrice());
            dto.setMrp(v.getMrp());
            dto.setBrand(v.getBrand());
            // pick first image
            if (!v.getProductImages().isEmpty()) {
                dto.setImageUrl(v.getProductImages().get(0).getImageUrl());
            }

            return dto;
        }).toList();
    }

    @Override
    public ProductVariantUserOrderResponseDto
    getVariantById(Long id) {

        ProductVariant variant = productVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        ProductVariantUserOrderResponseDto dto = new ProductVariantUserOrderResponseDto();

        dto.setProductVariantId(variant.getProductVariantId());
        dto.setSellingPrice(variant.getSellingPrice());
        dto.setMrp(variant.getMrp());

        // Product name (from parent entity)
        dto.setProductName(variant.getProduct().getName());

        // Image (first image)
        if (variant.getProductImages() != null && !variant.getProductImages().isEmpty()) {
            dto.setImageUrl(variant.getProductImages().get(0).getImageUrl());
        }

        return dto;
    }

    @Override
    public List<ProductVariantUserOrderResponseDto> getOrderVariantsByIds(List<Long> variantIds) {

        List<ProductVariant> variants = productVariantRepository.findByProductVariantIdIn(variantIds);

        return variants.stream().map(variant -> {

            ProductVariantUserOrderResponseDto dto =
                    new ProductVariantUserOrderResponseDto();

            dto.setProductVariantId(variant.getProductVariantId());
            dto.setProductName(variant.getProduct().getName());
            dto.setSkuCode(variant.getSkuCode());
            dto.setBrand(variant.getBrand());
            dto.setColor(variant.getColor());
            dto.setSize(variant.getSize());
            dto.setSellingPrice(variant.getSellingPrice());
            dto.setMrp(variant.getMrp());

            // Get primary image
            String imageUrl = null;

            if (variant.getProductImages() != null &&
                    !variant.getProductImages().isEmpty()) {

                imageUrl = variant.getProductImages().stream()
                        .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                        .map(img -> img.getImageUrl())
                        .findFirst()
                        .orElse(
                                variant.getProductImages().get(0).getImageUrl()
                        );
            }

            dto.setImageUrl(imageUrl);

            return dto;

        }).toList();
    }
}
