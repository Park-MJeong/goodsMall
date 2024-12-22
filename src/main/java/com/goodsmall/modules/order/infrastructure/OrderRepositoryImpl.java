package com.goodsmall.modules.order.infrastructure;

import com.goodsmall.modules.order.domain.OrderRepository;
import com.goodsmall.modules.order.domain.entity.Order;
import com.goodsmall.modules.order.domain.entity.OrderProducts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final JpaOrderRepository jpaOrderRepository;
    private final JpaOrderProductsRepository jpaOrderProductsRepository;

    @Override
    public Page<Order> getOrderList(Long userId, Pageable pageable) {
        return  jpaOrderRepository.findOrdersWithProducts(userId,pageable);
    }

}
