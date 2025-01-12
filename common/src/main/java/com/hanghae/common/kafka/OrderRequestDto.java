package com.hanghae.common.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OrderRequestDto {
    private long productId;
    private int quantity;

    @JsonCreator
    public OrderRequestDto(
            @JsonProperty("productId") long productId,
            @JsonProperty("quantity") int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
