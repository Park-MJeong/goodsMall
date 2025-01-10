package com.hanghae.orderservice.dto;

import com.hanghae.orderservice.client.dto.ProductNameAndPriceDTO;
import com.hanghae.orderservice.client.dto.ProductResponseDto;
import com.hanghae.orderservice.domain.entity.Order;
import com.hanghae.orderservice.domain.entity.OrderProducts;
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
     * 주문 내역 리스트 ( 물품 구매시 )
     * */
    public OrderListDto(OrderProducts orderProducts, ProductNameAndPriceDTO productNameAndPriceDTO) {
        this.id =orderProducts.getOrder().getId();
        this.orderDate = orderProducts.getOrder().getUpdatedAt();
        this.totalPrice =orderProducts.getOrder().getTotalPrice();
        this.status = orderProducts.getOrder().getStatus();
        this.products = List.of(new OrderProductDto(orderProducts, productNameAndPriceDTO));
    }

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

