package com.mahalaxmi.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponseDTO {

    private String name;

    private String email;

    private String countryCode;
    private String phone;

    private List<UserAddressResponseDTO> userAddressResponseDTOList;

}
