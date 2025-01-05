package com.hanghae.orderservice.infrastructure;

import com.hanghae.orderservice.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface jpaPaymentRepository extends JpaRepository<Payment, Long> {
}
