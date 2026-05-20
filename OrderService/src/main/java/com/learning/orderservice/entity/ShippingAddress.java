package com.learning.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ShippingAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String mobileNumber;
    private String addressLine;
    private String city;
    private String state;
    private String pincode;
    private String country;
}