package com.hanghae.common.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PaymentEvent {
    private Long orderId;
//    private List<OrderRequestDto> orderRequestDtoList;
    @JsonCreator
    public PaymentEvent(
            @JsonProperty("orderId") long orderId){
        this.orderId = orderId;}
}
