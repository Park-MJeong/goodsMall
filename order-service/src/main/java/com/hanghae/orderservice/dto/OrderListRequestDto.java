package com.hanghae.orderservice.dto;

import com.hanghae.common.kafka.OrderRequestDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderListRequestDto {
    private List<OrderRequestDto> orderRequestDtoList;


}

//public Order(Long userId) {
//    this.userId = userId;
//    this.status = OrderStatus.PROCESSING;
//    this.createdAt = LocalDateTime.now();
//    this.updatedAt = LocalDateTime.now();
//    this.totalPrice = BigDecimal.ZERO;
//}