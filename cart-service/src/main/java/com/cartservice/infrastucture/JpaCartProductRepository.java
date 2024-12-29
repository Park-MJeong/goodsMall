package com.cartservice.infrastucture;

import com.cartservice.domain.entity.Cart;
import com.cartservice.domain.entity.CartProducts;
import com.productservice.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCartProductRepository extends JpaRepository<CartProducts,Long> {
//    Boolean existsByCartAndProduct(Cart cart, Product product);

}
