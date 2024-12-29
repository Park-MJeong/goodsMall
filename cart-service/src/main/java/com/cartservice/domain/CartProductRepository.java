package com.cartservice.domain;

import com.cartservice.domain.entity.Cart;
import com.cartservice.domain.entity.CartProducts;
import com.productservice.domain.Product;

import java.util.Optional;

public interface CartProductRepository {
    Optional<CartProducts> getCartProducts(Long id);
    void save(CartProducts cartProducts);
    void delete(Long id);
//    boolean isProductAlreadyInCart(Cart cart, Product product);
}
