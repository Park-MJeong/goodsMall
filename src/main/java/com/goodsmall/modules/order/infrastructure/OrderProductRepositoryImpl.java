package com.goodsmall.modules.order.infrastructure;

import com.goodsmall.modules.order.domain.OrderProductRepository;
import com.goodsmall.modules.order.domain.entity.OrderProducts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {
    private final JpaOrderProductsRepository repository;
    @Override
    public Page<OrderProducts> getOrderProductList(Long orderId, Pageable pageable) {
        return repository.findOrdersWithProducts(orderId,pageable);
    }
}
