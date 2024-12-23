package com.goodsmall.modules.cart.infrastucture;

import com.goodsmall.common.constant.ErrorCode;
import com.goodsmall.common.exception.BusinessException;
import com.goodsmall.modules.cart.domain.CartProductRepository;
import com.goodsmall.modules.cart.domain.entity.CartProducts;
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
}
