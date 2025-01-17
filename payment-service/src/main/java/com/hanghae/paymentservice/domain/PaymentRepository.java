package com.hanghae.paymentservice.domain;

import com.hanghae.paymentservice.domain.entity.Payment;
import com.hanghae.paymentservice.domain.entity.PaymentStatus;
import com.hanghae.paymentservice.dto.PaymentStatusDto;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PaymentRepository {
    void save(Payment payment);
    void saveAll(List<Payment> payments);
    Optional<Payment> findById(Long id);
    Payment findByOrderId(Long orderId);
    PaymentStatusDto findStatusById(Long paymentId);

}
