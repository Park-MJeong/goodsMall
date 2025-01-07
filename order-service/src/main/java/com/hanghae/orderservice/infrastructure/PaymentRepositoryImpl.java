package com.hanghae.orderservice.infrastructure;

import com.hanghae.orderservice.domain.PaymentRepository;
import com.hanghae.orderservice.domain.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final jpaPaymentRepository repository;
    @Override
    public void save(Payment payment) {
        repository.save(payment);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return repository.findById(id);
    }
    @Override
    public Payment findByOrderId(Long orderId) {
        return repository.findByOrderId(orderId);
    }
}
