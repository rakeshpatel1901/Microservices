package com.mahalaxmi.authservice.dto;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponseDto{
    private Long userId;
    private String name;
    private String email;
    private String countryCode;
    private String phone;
    private String role;
}
