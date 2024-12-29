package com.cartservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CartProductAddRequestDto {
    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long productId;

    @NotNull
    @Min(value = 1, message = "최소 1개 이상 담아주세요.")
    private Integer quantity;
}
