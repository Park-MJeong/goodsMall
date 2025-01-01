package com.cartservice.infrastucture;

import com.goodsmall.modules.cart.domain.CartRepository;
import com.goodsmall.modules.cart.domain.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CartRepositoryImpl implements CartRepository {
    private final JpaCartsRepository repository;



    @Override
    public void save(Cart cart) {
        repository.save(cart);
    }
    @Override
    public Cart getCartByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

}