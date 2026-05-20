package com.mahalaxmi.authservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequestDto {
    String countryCode;
    String phone;
    String code;
}
