package com.hanghae.cartservice.domain;

import com.hanghae.cartservice.domain.entity.Cart;

public interface CartRepository {

    void save(Cart cart);

    Cart getCartByUserId(Long cartId);
}
