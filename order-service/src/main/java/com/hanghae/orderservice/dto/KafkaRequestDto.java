package com.hanghae.orderservice.dto;

import com.hanghae.common.kafka.OrderRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class KafkaRequestDto {
    private Long orderId;
    private BigDecimal totalPrice;
    List<OrderRequestDto> orderRequestDtoList;
}
