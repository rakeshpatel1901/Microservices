package com.mahalaxmi.cart.controller;

import com.mahalaxmi.cart.dto.CartItemRequestDto;
import com.mahalaxmi.cart.dto.CartResponseDto;
import com.mahalaxmi.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    // Constructor Injection
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // 1. Add item to cart
    @PostMapping("/add")
    public ResponseEntity<String> addItem(@RequestBody CartItemRequestDto dto) {
        String response = cartService.addItem(dto);
        return ResponseEntity.ok(response);
    }

    // 2. Get cart details
    @GetMapping
    public ResponseEntity<CartResponseDto> getCart() {
        CartResponseDto cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }

    // 3. Update item quantity
    @PutMapping("/update")
    public ResponseEntity<String> updateItem(
            @RequestParam Long productVariantId,
            @RequestParam Integer quantity
    ) {
        String response = cartService.updateItem(productVariantId, quantity);
        return ResponseEntity.ok(response);
    }

    // 4. Remove item from cart
    @DeleteMapping("/remove/{productVariantId}")
    public ResponseEntity<String> removeItem(@PathVariable Long productVariantId) {
        System.out.println("Inside Remove item");
        String response = cartService.removeItem(productVariantId);
        return ResponseEntity.ok(response);
    }

    // 5. Clear cart
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        String response = cartService.clearCart();
        return ResponseEntity.ok(response);
    }

    // 6. Checkout cart
    @PostMapping("/checkout")
    public ResponseEntity<CartResponseDto> checkout() {
        CartResponseDto response = cartService.checkout();
        return ResponseEntity.ok(response);
    }
}