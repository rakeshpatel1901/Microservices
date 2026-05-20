package com.mahalaxmi.authservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Returned by order-service's /api/internal/orders/user-stats endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderStatsDTO {
    private Long       userId;
    private Integer    orderCount;   // non-cancelled orders
    private BigDecimal totalSpent;   // sum of totalAmount for those orders
}

