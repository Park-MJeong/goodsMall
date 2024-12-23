package com.goodsmall.modules.cart.infrastucture;

import com.goodsmall.modules.cart.domain.entity.Cart;
import com.goodsmall.modules.cart.domain.entity.CartProducts;
import com.goodsmall.modules.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCartProductRepository extends JpaRepository<CartProducts,Long> {
    Boolean existsByCartAndProduct(Cart cart,Product product);

}
