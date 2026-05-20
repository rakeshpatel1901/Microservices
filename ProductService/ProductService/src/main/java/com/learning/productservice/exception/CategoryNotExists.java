package com.learning.productservice.exception;

public class CategoryNotExists extends RuntimeException {
    public CategoryNotExists(String message) {
        super(message);
    }
}
