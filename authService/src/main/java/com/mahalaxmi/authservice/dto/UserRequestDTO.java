package com.mahalaxmi.authservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequestDTO {

    @NotNull(message="Name cannot be empty")
    @NotEmpty(message="Name must not be empty")
    @NotBlank(message="Name must not be blank")
    private String name;


    @NotNull(message="Email must not be null")
    @Email(message="Email is invalid!")
    private String email;

    @NotNull(message="Phone number must not be null")
    @Size(min=10 , max = 10, message="Phone number must be 10-digit")
    private String phone;

    @NotNull(message = "Country Code cannot be null")
    @NotEmpty(message = "Country Code cannot be empty")
    @NotBlank(message = "Country Code cannot be blank")
    private String countryCode;

    @Size(min=6,max=15,message="Password length should be 6-15")
    private String password;

    @Null(message="You cannot determine the role")
    private String role;

    private List<UserAddressResponseDTO> userAddressResponseDTOList;
}
