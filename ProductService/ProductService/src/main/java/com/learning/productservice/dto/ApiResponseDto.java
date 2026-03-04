package com.learning.productservice.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseDto<T> {
    String status;
    Integer statusCode;
    String message;
    T data;
}
