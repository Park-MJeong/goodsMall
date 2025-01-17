package com.hanghae.paymentservice.infrastructure;

import com.hanghae.paymentservice.domain.PaymentRepository;
import com.hanghae.paymentservice.domain.entity.Payment;
import com.hanghae.paymentservice.domain.entity.PaymentStatus;
import com.hanghae.paymentservice.dto.PaymentStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final JpaPaymentRepository repository;

    @Override
    public void save(Payment payment) {
        repository.save(payment);
    }

    @Override
    public void saveAll(List<Payment> payments) {
        repository.saveAll(payments);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return repository.findById(id);
    }
    @Override
    public Payment findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId);
    }

    @Override
    public PaymentStatusDto findStatusById(Long paymentId) {
        return repository.findStatusById(paymentId);
    }

}
