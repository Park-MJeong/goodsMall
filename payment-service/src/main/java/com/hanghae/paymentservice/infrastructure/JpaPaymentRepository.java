package com.hanghae.paymentservice.infrastructure;

import com.hanghae.paymentservice.domain.entity.Payment;
import com.hanghae.paymentservice.dto.PaymentStatusDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaPaymentRepository extends JpaRepository<Payment,Long> {
    Payment findByOrderId(Long orderId);

    @Query("SELECT new com.hanghae.paymentservice.dto.PaymentStatusDto(p.orderId,p.status) FROM Payment p WHERE p.id = :paymentId")
    PaymentStatusDto findStatusById(@Param("paymentId") Long paymentId);
}
