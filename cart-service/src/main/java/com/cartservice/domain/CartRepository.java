package com.cartservice.domain;

import com.goodsmall.modules.cart.domain.entity.Cart;


public interface CartRepository {

    void save(Cart cart);

    Cart getCartByUserId(Long cartId);
}
