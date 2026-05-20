package com.payment.payment_service.repository;

import com.payment.payment_service.entity.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payments, Long> {
    Optional<Payments> findByRazorpayOrderId(String razorpayOrderId);
    List<Payments> findByOrderIdIn(List<Long> orderIds);
}
