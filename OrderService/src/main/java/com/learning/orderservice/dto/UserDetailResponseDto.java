package com.learning.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailResponseDto {
    private Long userId;
    private String name;
    private String email;
    private String countryCode;
    private String phone;
    private String role;
}
