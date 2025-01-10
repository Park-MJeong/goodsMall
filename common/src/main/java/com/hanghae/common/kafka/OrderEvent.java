package com.hanghae.common.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class OrderEvent {
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal totalPrice;
    @JsonCreator
    public OrderEvent(
            @JsonProperty("orderId") Long orderId,
            @JsonProperty("productId") Long productId,
            @JsonProperty("quantity") Integer quantity,
            @JsonProperty("totalPrice") BigDecimal totalPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

}
