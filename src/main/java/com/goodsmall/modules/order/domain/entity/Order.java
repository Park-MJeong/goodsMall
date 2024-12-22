package com.goodsmall.modules.order.domain.entity;

import com.goodsmall.modules.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreatedDate
    @Column(name = "creat_At", nullable = false)
    private LocalDateTime creatAt;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @Column(name="status",nullable = false)
    private String status;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderProducts> orderProducts;

    @ManyToOne(fetch = FetchType.LAZY) // N:1 관계로 User를 참조
    @JoinColumn(name = "user_id", nullable = false) // 외래키 설정
    private User user;


}
