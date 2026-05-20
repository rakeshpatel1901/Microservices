package com.mahalaxmi.cart.repository;

import com.mahalaxmi.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCart_IdAndProductVariantId(Long cartId, Long productVariantId);

    void deleteByCart_IdAndProductVariantId(Long cartId, Long productVariantId);
}
