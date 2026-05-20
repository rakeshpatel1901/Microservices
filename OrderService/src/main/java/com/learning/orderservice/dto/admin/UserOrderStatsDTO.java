package com.learning.orderservice.dto.admin;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderStatsDTO {
    private Long       userId;
    private Integer    orderCount;
    private BigDecimal totalSpent;
}

