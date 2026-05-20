package com.mahalaxmi.user.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAddressResponseDTO {
    private Long userAddressId;
    private String addressLine;

    private String city;

    private String state;

    private String pincode;

    private Boolean isDefault;
}
