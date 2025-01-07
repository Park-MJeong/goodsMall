package com.hanghae.orderservice.domain.entity;

import com.hanghae.orderservice.util.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name="status",nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CreatedDate
    @Column(name = "created_At", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_At", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderProducts> orderProducts;

    private Long userId;

    public Order(){}

    public Order(Long userId) {
        this.userId = userId;
        this.status = OrderStatus.PROCESSING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.totalPrice = BigDecimal.ZERO;
    }


    public void updatePrice(BigDecimal price) {
        this.totalPrice = price;
    }
    public void statusComplete(){
        this.status = OrderStatus.COMPLETE;
        this.updatedAt = LocalDateTime.now();
    }
    public void statusFailed(){
        this.status = OrderStatus.FAILED;
        this.updatedAt = LocalDateTime.now();
    }
    public void statusCancel(){
        this.status = OrderStatus.CANCELED;
        this.updatedAt = LocalDateTime.now();
    }
//

}
