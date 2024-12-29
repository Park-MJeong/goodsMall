package com.orderservice.dto;

import com.orderservice.OrderStatus;
import com.orderservice.domain.entity.Order;
import com.orderservice.domain.entity.OrderProducts;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderListDto {
    private Long id;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private List<OrderProductDto> products;

    /**
     * 주문 내역 리스트
     * */
    public OrderListDto(Order order) {
        this.id = order.getId();
        this.orderDate = order.getCreatedAt();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus();

        this.products = order.getOrderProducts().stream()
                .map(OrderProductDto::new)
                .collect(Collectors.toList());
    }
    public OrderListDto(OrderProducts orderProducts) {
        this.id = orderProducts.getOrder().getId();
        this.orderDate = orderProducts.getOrder().getCreatedAt();
        this.totalPrice = orderProducts.getOrder().getTotalPrice();
        this.status = orderProducts.getOrder().getStatus();
        this.products =List.of(new OrderProductDto(orderProducts));
    }

    public OrderListDto(Order order,List<OrderProductDto> products) {
        this.id = order.getId();
        this.orderDate = order.getCreatedAt();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus();
        this.products = products;
    }

}

