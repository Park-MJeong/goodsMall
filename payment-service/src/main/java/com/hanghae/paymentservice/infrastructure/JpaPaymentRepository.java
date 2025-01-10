package com.hanghae.paymentservice.infrastructure;

import com.hanghae.paymentservice.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPaymentRepository extends JpaRepository<Payment,Long> {
    Payment findByOrderId(Long orderId);
}
