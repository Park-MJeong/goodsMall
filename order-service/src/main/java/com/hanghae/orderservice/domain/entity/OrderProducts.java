package com.hanghae.orderservice.domain.entity;

import com.hanghae.orderservice.client.dto.ProductResponseDto;
import com.hanghae.orderservice.dto.Order.OrderRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "order_products")
public class OrderProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="quantity",nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private long productId;

    public OrderProducts() {}

    public OrderProducts(Order order, OrderRequestDto orderRequestDto,BigDecimal price) {
        this.order = order;
        this.productId = orderRequestDto.getProductId();
        this.quantity = orderRequestDto.getQuantity();
        this.price =price;
    }
    public void saveOrderProducts(Order order,Long productId,int quantity,BigDecimal price) {
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
}
