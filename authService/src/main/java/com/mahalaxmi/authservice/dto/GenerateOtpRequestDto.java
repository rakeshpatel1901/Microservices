package com.mahalaxmi.authservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateOtpRequestDto {

    @NotNull(message = "Country Code cannot be null")
    @NotEmpty(message = "Country Code cannot be empty")
    @NotBlank(message = "Country Code cannot be blank")
    String countryCode;

    @NotNull(message = "Phone number cannot be null")
    @NotEmpty(message = "Phone number cannot be empty")
    @NotBlank(message = "Phone number cannot be blank")
    String phone;
}
