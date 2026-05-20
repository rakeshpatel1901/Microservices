package com.mahalaxmi.authservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AdminUserDTO {

    private Long        userId;
    private String      name;
    private String      email;
    private String      countryCode;
    private String      phone;
    private String      role;
    private LocalDateTime createdAt;
    private Integer     orderCount;
    private BigDecimal totalSpent;
}
