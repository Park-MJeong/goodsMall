package com.goodsmall.modules.order.domain;

import com.goodsmall.modules.order.OrderStatus;
import com.goodsmall.modules.order.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;


public interface OrderRepository {
    Page<Order> getOrderList(Long userId, Pageable pageable);
    Order getOrderProductsList(Long orderId);
    void save(Order order);
    List<Order> findByStatus(OrderStatus status, LocalDateTime date);



}
