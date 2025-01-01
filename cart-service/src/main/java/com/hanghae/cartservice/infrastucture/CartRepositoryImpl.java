package com.hanghae.cartservice.infrastucture;

import com.hanghae.cartservice.domain.CartRepository;
import com.hanghae.cartservice.domain.entity.Cart;
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
