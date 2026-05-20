package com.learning.orderservice.repository;

import com.learning.orderservice.dto.types.OrderStatus;
import com.learning.orderservice.entity.Orders;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByRazorpayOrderId(String razorpayOrderId);

    @Query("""
                SELECT COUNT(oi) > 0 
                FROM OrderItem oi 
                JOIN oi.order o 
                WHERE o.userId = :userId 
                  AND oi.productVariantId = :productVariantId 
            """)
    Boolean existsByUserAndProduct(Long userId, Long productVariantId);

    List<Orders> findByUserId(Long userId);


    Page<Orders> findByStatus(OrderStatus orderStatus, Pageable pageable);


    @Query("""
                SELECT o.status, COUNT(o)
                FROM Orders o
                GROUP BY o.status
            """)
    List<Object[]> countByStatus();

    @Query("""
            SELECT o.userId, COUNT(o.orderId), SUM(o.totalAmount)
            FROM Orders o
            WHERE o.userId IN :userIds
              AND o.status NOT IN :excluded
            GROUP BY o.userId
            """)
    List<Object[]> findUserOrderStats(
            @Param("userIds") List<Long> userIds,
            @Param("excluded") Set<OrderStatus> excluded
    );

}
