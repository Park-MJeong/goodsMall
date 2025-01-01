package com.hanghae.cartservice.dto;


import com.hanghae.cartservice.domain.entity.Cart;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
public class CartListDto {
    private Long id;
    private BigDecimal totalPrice;
    private List<CartProductDto> products;

    public CartListDto(Cart cart,List<CartProductDto> cartProductDtoList) {
        this.id = cart.getId();
        this.products = cartProductDtoList;
        this.totalPrice = cartProductDtoList.stream()
                .map(CartProductDto::getTotalProductPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
