package com.hanghae.orderservice.domain.entity;

import com.hanghae.orderservice.client.dto.ProductResponseDto;
import com.hanghae.orderservice.dto.OrderRequestDto;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "order_products")
public class OrderProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="quantity",nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private long productId;

    public OrderProducts() {}

    public OrderProducts(Order order, OrderRequestDto orderRequestDto, ProductResponseDto responseDto) {
        System.out.println(order.getId());
        this.order = order;
        this.productId = orderRequestDto.getProductId();
        this.quantity = orderRequestDto.getQuantity();
        this.price = responseDto.getProductPrice();
    }
    public void saveOrderProducts(Order order,Long productId,int quantity,BigDecimal price) {
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }
}
