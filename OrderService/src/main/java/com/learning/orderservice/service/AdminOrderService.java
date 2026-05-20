package com.learning.orderservice.service;

import com.learning.orderservice.dto.admin.AdminOrderDTO;
import com.learning.orderservice.dto.admin.AdminOrderFilterRequest;
import com.learning.orderservice.dto.admin.AdminOrderSummaryDTO;
import com.learning.orderservice.dto.types.OrderStatus;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface AdminOrderService {
    public Page<AdminOrderSummaryDTO> getOrdersPage(AdminOrderFilterRequest req);
    public AdminOrderDTO getOrderDetail(Long orderId);
    public AdminOrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus);
    public Map<String, Long> getStatusCounts();

}
