package com.hanghae.paymentservice.domain;

import com.hanghae.paymentservice.domain.entity.Payment;

import java.util.Optional;


public interface PaymentRepository {
    void save(Payment payment);
    Optional<Payment> findById(Long id);
    Payment findByOrderId(Long orderId);

}
