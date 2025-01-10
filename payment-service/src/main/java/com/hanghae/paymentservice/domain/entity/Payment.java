package com.hanghae.paymentservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
@EntityListeners(AuditingEntityListener.class)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @CreatedDate
    @Column(name = "created_At", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_At", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name="status",nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public Payment(Long orderId) {
        this.orderId = orderId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }



    public void statusFail(){
        this.status = PaymentStatus.FAILED;
    }
    public void statusCancel(){
        this.status = PaymentStatus.CANCELED;
    }
    public void statusComplete(){
        this.status = PaymentStatus.COMPLETE;
    }
}
