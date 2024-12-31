package com.cartservice.infrastucture;

import com.goodsmall.modules.cart.domain.CartProductRepository;
import com.goodsmall.modules.cart.domain.entity.Cart;
import com.goodsmall.modules.cart.domain.entity.CartProducts;
import com.goodsmall.modules.product.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CartProductRepositoryImpl implements CartProductRepository {
    private final JpaCartProductRepository repository;
    @Override
    public Optional<CartProducts> getCartProducts(Long id) {
        return repository.findById(id);
    }

    @Override
    public void save(CartProducts cartProducts) {
        repository.save(cartProducts);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean isProductAlreadyInCart(Cart cart, Product product) {
        return repository.existsByCartAndProduct(cart,product);
    }
}
