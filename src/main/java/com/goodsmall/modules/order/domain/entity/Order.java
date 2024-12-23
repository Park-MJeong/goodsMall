package com.goodsmall.modules.order.domain.entity;

import com.goodsmall.modules.order.OrderStatus;
import com.goodsmall.modules.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name="status",nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CreationTimestamp
    @Column(name = "created_At", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_At", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderProducts> orderProducts;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY) // N:1 관계로 User를 참조
    @JoinColumn(name = "user_id", nullable = false) // 외래키 설정
    private User user;


    public Order(){}

    public Order(User user) {
        this.user = user;
        this.status = OrderStatus.PROCESSING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.totalPrice = BigDecimal.ZERO;
    }


}
