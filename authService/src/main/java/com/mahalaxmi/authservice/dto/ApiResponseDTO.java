package com.mahalaxmi.authservice.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseDTO<T>{
    String status;
    String statusCode;
    String message;
    T data;
}
