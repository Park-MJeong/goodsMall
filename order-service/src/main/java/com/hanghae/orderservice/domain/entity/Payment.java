package com.hanghae.orderservice.domain.entity;

import com.hanghae.orderservice.event.OrderStatus;
import com.hanghae.orderservice.event.PaymentStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PaymentStatus status;

    public Payment(Long orderId) {
        this.orderId = orderId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = PaymentStatus.PROCESSING;
    }

    public Payment(Long id,Long orderId, LocalDateTime createdAt, LocalDateTime updatedAt, PaymentStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;

    }

    public Payment() {

    }

    public void statusFail(){
        this.status = PaymentStatus.FAILED;
    }

    public void statusCompile(){
        this.status = PaymentStatus.COMPLETE;
    }
}
