package com.mahalaxmi.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("PRODUCT-SERVICE", r -> r
                        .path("/api/v1/products/**", "/api/v1/products/productVariant/**","/api/categories/**",
                                "/api/product/v1/admin/**")
                        .uri("lb://PRODUCT-SERVICE"))

                .route("payment-service", r -> r
                        .path("/api/payment/**")
                        .uri("lb://PAYMENT-SERVICE"))

                .route("ORDER-SERVICE", r -> r
                        .path("/api/v1/orders/**","/shipping/**","/api/admin/orders/**")
                        .uri("lb://ORDER-SERVICE"))

                .route("AUTH-SERVICE", r -> r
                        .path("/api/auth/**","/api/admin/users/**")
                        .uri("lb://AUTH-SERVICE"))

                .route("NOTIFICATION-SERVICE", r -> r
                        .path("/api/notification/**")
                        .uri("lb://NOTIFICATION-SERVICE"))
                .route("USER-SERVICE", r-> r
                        .path("/api/addresses/**")
                        .uri("lb://USER-SERVICE"))
                .route("CART-SERVICE", r -> r
                        .path("/api/v1/cart/**")
                        .uri("lb://CART-SERVICE"))
                .route("USER-SERVICE", r-> r
                        .path("/api/addresses/**")
                        .uri("lb://USER-SERVICE"))
                .build();
    }
}
