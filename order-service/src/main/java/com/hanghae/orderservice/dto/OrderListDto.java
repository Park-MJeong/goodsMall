package com.hanghae.orderservice.dto;

import com.hanghae.orderservice.domain.entity.Order;
import com.hanghae.orderservice.domain.entity.OrderStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderListDto {
    private Long id;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private List<OrderProductDto> products;


    /**
     * 주문 내역  조회
     * */
    public OrderListDto(Order order, List<OrderProductDto> orderProductDtoList) {
        this.id = order.getId();
        this.orderDate = order.getCreatedAt();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus();
        this.products = orderProductDtoList;
    }
}

