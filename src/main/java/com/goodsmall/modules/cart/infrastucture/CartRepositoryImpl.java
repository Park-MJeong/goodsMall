package com.goodsmall.modules.cart.infrastucture;

import com.goodsmall.modules.cart.domain.CartRepository;
import com.goodsmall.modules.cart.domain.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {
    private final JpaCartsRepository repository;

    @Override
    public Cart getCart(Long cartId) {
        return repository.findCartById(cartId);
    }

    @Override
    public void save(Cart cart) {
        repository.save(cart);
    }
}
