package com.mahalaxmi.user.entity;

import com.mahalaxmi.user.dto.types.IndianState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAddressId;

    private String addressLine;

    private String city;

    @Enumerated(EnumType.STRING)
    private IndianState state;

    private String pincode;

    private Boolean isDefault;

    private Long userId;
}
