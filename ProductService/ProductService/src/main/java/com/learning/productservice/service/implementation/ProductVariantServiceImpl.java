package com.learning.productservice.service.implementation;

import com.learning.productservice.dto.ProductVariantOrderResponseDto;
import com.learning.productservice.entity.Product;
import com.learning.productservice.entity.ProductVariant;
import com.learning.productservice.mapper.ProductVariantMapper;
import com.learning.productservice.repository.ProductVariantRepository;
import com.learning.productservice.service.ProductVariantService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
