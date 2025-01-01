package com.hanghae.cartservice.infrastucture;


import com.hanghae.cartservice.domain.entity.Cart;
import com.hanghae.cartservice.domain.entity.CartProducts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCartProductRepository extends JpaRepository<CartProducts,Long> {

    Boolean existsByCartAndProductId(Cart cart,Long productId);

}
