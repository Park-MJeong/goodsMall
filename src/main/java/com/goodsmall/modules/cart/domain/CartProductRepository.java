package com.goodsmall.modules.cart.domain;

import com.goodsmall.modules.cart.domain.entity.CartProducts;

import java.util.Optional;

public interface CartProductRepository {
    Optional<CartProducts> getCartProducts(Long id);
    void save(CartProducts cartProducts);
    void delete(Long id);
}
