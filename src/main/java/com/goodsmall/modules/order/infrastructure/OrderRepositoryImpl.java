package com.goodsmall.modules.order.infrastructure;

import com.goodsmall.modules.order.domain.OrderRepository;
import com.goodsmall.modules.order.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final JpaOrderRepository jpaOrderRepository;

    @Override
    public Page<Order> getOrderList(Long userId, Pageable pageable) {
        return  jpaOrderRepository.findOrdersWithProducts(userId,pageable);
    }

    @Override
    public Order getOrderProductsList(Long orderId) {
        return jpaOrderRepository.findOrderById(orderId);
    }

    @Override
    public void save(Order order) {
        jpaOrderRepository.save(order);
    }


}
