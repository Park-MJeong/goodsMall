package com.hanghae.cartservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartProductUpdateRequestDto {
    private Integer quantity;

    @JsonProperty("isDelete")
    private boolean isDelete;
}
