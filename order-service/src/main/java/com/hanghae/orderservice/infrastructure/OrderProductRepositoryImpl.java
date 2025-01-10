package com.hanghae.orderservice.infrastructure;


import com.hanghae.orderservice.domain.OrderProductRepository;
import com.hanghae.orderservice.domain.entity.Order;
import com.hanghae.orderservice.domain.entity.OrderProducts;
import com.hanghae.orderservice.dto.OrderProductStock;
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

    @Override
    public List<OrderProductStock> findStockByOrderId(Long orderId) {
        return repository.findStockByOrderId(orderId);
    }
}
