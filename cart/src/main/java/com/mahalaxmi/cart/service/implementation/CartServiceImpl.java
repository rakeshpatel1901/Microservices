package com.mahalaxmi.cart.service.implementation;

import com.mahalaxmi.cart.dto.*;
import com.mahalaxmi.cart.entity.Cart;
import com.mahalaxmi.cart.entity.CartItem;
import com.mahalaxmi.cart.externalservices.AuthClient;
import com.mahalaxmi.cart.externalservices.OrderClient;
import com.mahalaxmi.cart.externalservices.ProductClient;
import com.mahalaxmi.cart.repository.CartItemRepository;
import com.mahalaxmi.cart.repository.CartRepository;
import com.mahalaxmi.cart.service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AuthClient authClient;
    private final OrderClient orderClient;
    private final ProductClient productClient;
    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           AuthClient authClient,
                           OrderClient orderClient,
                           ProductClient productClient) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.authClient = authClient;
        this.orderClient = orderClient;
        this.productClient = productClient;
    }

    private Long getUserId() {
        return authClient.getUserId();
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public String addItem(CartItemRequestDto dto) {

        Long userId = getUserId();
        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> existingItem =
                cartItemRepository.findByCart_IdAndProductVariantId(
                        cart.getId(), dto.getProductVariantId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + dto.getQuantity());
        } else {
            CartItem item = new CartItem();
            item.setProductVariantId(dto.getProductVariantId());
            item.setQuantity(dto.getQuantity());
            item.setCart(cart);
            cart.getItems().add(item);
        }

        cartRepository.save(cart);
        return "Item added to cart";
    }


    @Override
    public CartResponseDto getCart() {

        Long userId = getUserId();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // ✅ Step 1: Collect all variant IDs
        List<Long> variantIds = cart.getItems().stream()
                .map(CartItem::getProductVariantId)
                .toList();

        // ✅ Step 2: Call Product Service (bulk)
        List<ProductVariantCartResponseDto> variants =
                productClient.getVariantsByIds(variantIds);

        // ✅ Step 3: Convert list → map for fast lookup
        Map<Long, ProductVariantCartResponseDto> variantMap =
                variants.stream().collect(Collectors.toMap(
                        ProductVariantCartResponseDto::getProductVariantId,
                        v -> v
                ));

        // ✅ Step 4: Build enriched cart items
        List<CartItemDto> items = cart.getItems().stream().map(item -> {

            CartItemDto dto = new CartItemDto();
            dto.setProductVariantId(item.getProductVariantId());
            dto.setQuantity(item.getQuantity());

            ProductVariantCartResponseDto variant =
                    variantMap.get(item.getProductVariantId());

            if (variant != null) {
                dto.setProductName(variant.getProductName());
                dto.setSellingPrice(variant.getSellingPrice());
                dto.setMrp(variant.getMrp());
                dto.setImageUrl(variant.getImageUrl());

                // combine color + size
                dto.setVariantName(
                        (variant.getColor() != null ? variant.getColor() : "") + " " +
                                (variant.getSize() != null ? variant.getSize() : "")
                );

                dto.setBrand(variant.getBrand()); // optional
            }

            return dto;

        }).toList();


        CartResponseDto response = new CartResponseDto();
        response.setUserId(userId);
        response.setItems(items);

        return response;
    }

    @Override
    public String updateItem(Long productVariantId, Integer quantity) {

        Long userId = getUserId();
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository
                .findByCart_IdAndProductVariantId(cart.getId(), productVariantId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (quantity == 0) {
            cartItemRepository.delete(item);
            return "Item removed";
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return "Item updated";
    }

    @Transactional
    @Override
    public String removeItem(Long productVariantId) {

        Long userId = getUserId();
        Cart cart = getOrCreateCart(userId);

        cartItemRepository.deleteByCart_IdAndProductVariantId(
                cart.getId(), productVariantId);

        return "Item removed";
    }

    @Override
    public String clearCart() {

        Long userId = getUserId();
        Cart cart = getOrCreateCart(userId);

        cart.getItems().clear();
        cartRepository.save(cart);

        return "Cart cleared";
    }




    @Override
    public CartResponseDto checkout() {

        Long userId = getUserId();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }


        List<OrderItemRequestDto> orderItems = cart.getItems().stream().map(item -> {
            OrderItemRequestDto dto = new OrderItemRequestDto();
            dto.setProductVariantId(item.getProductVariantId());
            dto.setQuantity(item.getQuantity());
            return dto;
        }).toList();

        OrderRequestDto orderRequest = new OrderRequestDto();
        orderRequest.setItems(orderItems);


        OrderResponseDto response = orderClient.createOrder(orderRequest);


        cart.getItems().clear();
        cartRepository.save(cart);

        return getCart();
    }


}