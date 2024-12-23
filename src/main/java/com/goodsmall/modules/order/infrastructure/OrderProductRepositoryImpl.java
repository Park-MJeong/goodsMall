package com.goodsmall.modules.order.infrastructure;

import com.goodsmall.modules.order.domain.OrderProductRepository;
import com.goodsmall.modules.order.domain.entity.OrderProducts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {
    private final JpaOrderProductsRepository repository;

    @Override
    public void save(OrderProducts orderProducts) {
        repository.save(orderProducts);
    }
}
