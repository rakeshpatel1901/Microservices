package com.learning.orderservice.repository;

import com.learning.orderservice.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByRazorpayOrderId(String razorpayOrderId);
}
