package com.hanghae.cartservice.infrastucture;

import com.hanghae.cartservice.domain.CartProductRepository;
import com.hanghae.cartservice.domain.entity.Cart;
import com.hanghae.cartservice.domain.entity.CartProducts;
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
    public boolean isProductAlreadyInCart(Cart cart, Long productId) {
        return repository.existsByCartAndProductId(cart,productId);
    }
}
