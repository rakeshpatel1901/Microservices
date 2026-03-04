package com.learning.productservice.exception;


import com.learning.productservice.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponseDto<String> handleIllegalArgumentException(String msg){
        ApiResponseDto<String> apiResponseDto = new ApiResponseDto<>();
        apiResponseDto.setMessage(msg);
        apiResponseDto.setStatus("FAILED");
        return apiResponseDto;
    }

}
