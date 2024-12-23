package com.goodsmall.modules.cart.dto;

import com.goodsmall.modules.cart.domain.entity.Cart;
import com.goodsmall.modules.cart.domain.entity.CartProducts;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CartListDto {
    private Long id;
    private int totalQuantity;
    private BigDecimal totalPrice;
    private List<CartProductDto> products;

    public CartListDto(Cart cart) {
        this.id = cart.getId();
        this.totalQuantity = cart.getCartProducts().stream()
                .mapToInt(CartProducts::getQuantity)
                .sum();
        this.products = cart.getCartProducts().stream()
                .map(CartProductDto::new)
                .collect(Collectors.toList());
        this.totalPrice = cart.getCartProducts().stream()
                .map(cp->cp.getProduct().getProductPrice().multiply(BigDecimal.valueOf(cp.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
