package com.learning.orderservice.controller.admin;


import com.learning.orderservice.dto.admin.AdminOrderDTO;
import com.learning.orderservice.dto.admin.AdminOrderFilterRequest;
import com.learning.orderservice.dto.admin.AdminOrderSummaryDTO;
import com.learning.orderservice.dto.types.OrderStatus;
import com.learning.orderservice.service.AdminOrderService;
import com.learning.orderservice.service.implementation.AdminOrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderServiceImpl;
    @GetMapping
    public ResponseEntity<Page<AdminOrderSummaryDTO>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String search
    ) {
        AdminOrderFilterRequest req = new AdminOrderFilterRequest();
        req.setPage(page);
        req.setSize(Math.min(size, 20));   // cap at 20 rows per page
        req.setStatus(status);
        req.setSearch(search);
        return ResponseEntity.ok(adminOrderServiceImpl.getOrdersPage(req));
    }

    /**
     * GET /api/admin/orders/{orderId}
     * Returns the full order detail including all line items + shipment.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<AdminOrderDTO> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(adminOrderServiceImpl.getOrderDetail(orderId));
    }

    /**
     * PATCH /api/admin/orders/{orderId}/status
     * Body: { "status": "SHIPPED" }
     * Allows the admin to advance or cancel an order.
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<AdminOrderDTO> updateStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body
    ) {
        OrderStatus newStatus = OrderStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(adminOrderServiceImpl.updateOrderStatus(orderId, newStatus));
    }

    /**
     * GET /api/admin/orders/stats/counts
     * Returns a map like { "PENDING": 4, "SHIPPED": 12, ... }
     * Used to populate the stat cards at the top of the Orders tab.
     */
    @GetMapping("/stats/counts")
    public ResponseEntity<Map<String, Long>> getStatusCounts() {
        return ResponseEntity.ok(adminOrderServiceImpl.getStatusCounts());
    }
}