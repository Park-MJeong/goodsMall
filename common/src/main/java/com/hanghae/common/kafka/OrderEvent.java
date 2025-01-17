package com.hanghae.common.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
public class OrderEvent {
    private Long orderId;
    private BigDecimal totalPrice;
    private List<OrderRequestDto> orderRequestDtoList;
    @JsonCreator
    public OrderEvent(
            @JsonProperty("orderId") Long orderId,
            @JsonProperty("totalPrice") BigDecimal totalPrice,
            @JsonProperty("orderRequestDtoList") List<OrderRequestDto> orderRequestDtoList) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.orderRequestDtoList = orderRequestDtoList;
    }

}
