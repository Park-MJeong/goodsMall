package com.hanghae.orderservice.domain;

import com.hanghae.orderservice.domain.entity.Payment;

import java.util.Optional;

public interface PaymentRepository {
    void save(Payment payment);
    Optional<Payment> findById(Long id);
    Payment findByOrderId(Long orderId);
}
