package com.goodsmall.modules.cart.domain;

import com.goodsmall.modules.cart.domain.entity.Cart;


public interface CartRepository {
    Cart getCart(Long cartId);

    void save(Cart cart);

    Cart getCartByUserId(Long cartId);
}
