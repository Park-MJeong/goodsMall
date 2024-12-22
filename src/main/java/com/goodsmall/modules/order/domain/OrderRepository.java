package com.goodsmall.modules.order.domain;

import com.goodsmall.modules.order.domain.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;



public interface OrderRepository {
    Slice<Order> findOrdersWithProducts(Long userId, Long cursor, Pageable pageable);

}
