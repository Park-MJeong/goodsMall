package com.goodsmall.modules.order.infrastructure;

import com.goodsmall.modules.order.domain.OrderRepository;
import com.goodsmall.modules.order.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final JpaOrderRepository jpaOrderRepository;

    @Override
    public Slice<Order> findOrdersWithProducts(Long userId,Long cursor, Pageable pageable) {
        return  jpaOrderRepository.findOrdersWithProducts(userId,cursor,pageable);
    }
}
