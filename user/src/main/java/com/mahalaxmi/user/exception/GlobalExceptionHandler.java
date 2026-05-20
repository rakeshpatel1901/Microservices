package com.mahalaxmi.user.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<String> handleBadCredentialsException(InvalidDataException e){
        return ResponseEntity.status(401).body(e.getMessage());
    }
}
