package com.hanghae.cartservice.domain;
import com.hanghae.cartservice.domain.entity.Cart;
import com.hanghae.cartservice.domain.entity.CartProducts;


import java.util.Optional;

public interface CartProductRepository {
    Optional<CartProducts> getCartProducts(Long id);
    void save(CartProducts cartProducts);
    void delete(Long id);
    boolean isProductAlreadyInCart(Cart cart,Long productId);
}
