package com.cartservice.domain;

import com.cartservice.domain.entity.Cart;

public interface CartRepository {
//    Cart getCart(Long cartId);

    void save(Cart cart);
}
