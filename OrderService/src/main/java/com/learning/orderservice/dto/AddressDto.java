package com.learning.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
    private String fullName;
    private String mobileNumber;
    private String addressLine;
    private String city;
    private String state;
    private String pincode;
    private String country;
}