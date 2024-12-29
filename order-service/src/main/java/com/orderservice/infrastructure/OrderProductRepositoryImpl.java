package com.orderservice.infrastructure;


import com.orderservice.domain.OrderProductRepository;
import com.orderservice.domain.entity.Order;
import com.orderservice.domain.entity.OrderProducts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {
    private final JpaOrderProductsRepository repository;

    @Override
    public void save(OrderProducts orderProducts) {
        repository.save(orderProducts);
    }

    @Override
    public List<OrderProducts> findByOrder(Order order) {
        return repository.findByOrder(order);
    }
}
