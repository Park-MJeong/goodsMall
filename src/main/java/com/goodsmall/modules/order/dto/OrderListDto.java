package com.goodsmall.modules.order.dto;

import com.goodsmall.modules.order.domain.entity.Order;
import com.goodsmall.modules.order.domain.entity.OrderProducts;
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
    private int totalQuantity;
    private String status;
    private List<OrderProductDto> products;

    /**
     * 주문 내역 리스트
     * */
    public OrderListDto(Order order) {
        this.id = order.getId();
        this.orderDate = order.getCreatAt();
        this.totalPrice = order.getTotalPrice();
        this.status = order.getStatus();
        this.totalQuantity = order.getOrderProducts().stream()
                .mapToInt(op -> Integer.parseInt(op.getQuantity()))
                .sum(); // 수량 합산
        this.products = order.getOrderProducts().stream()
                .map(op -> new OrderProductDto(op))
                .collect(Collectors.toList());
    }
    public OrderListDto(OrderProducts orderProducts) {
        this.id = orderProducts.getOrder().getId();
        this.orderDate = orderProducts.getOrder().getCreatAt();
        this.totalPrice = orderProducts.getOrder().getTotalPrice();
        this.status = orderProducts.getOrder().getStatus();
        this.totalQuantity = orderProducts.getOrder().getOrderProducts().stream()
                .mapToInt(op -> Integer.parseInt(op.getQuantity())) // 수량 합산
                .sum();
        this.products = orderProducts.getOrder().getOrderProducts().stream()
                .map(OrderProductDto::new)
                .distinct()
                .collect(Collectors.toList());
    }
}
