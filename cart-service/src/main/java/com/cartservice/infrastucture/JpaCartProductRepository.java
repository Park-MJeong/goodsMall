package com.cartservice.infrastucture;


import com.hanghae.productservice.domain.Product;

public interface JpaCartProductRepository extends JpaRepository<CartProducts,Long> {
    Boolean existsByCartAndProduct(Cart cart, Product product);

}
