package com.orderservice.infrastructure;

import com.orderservice.OrderStatus;
import com.orderservice.domain.OrderRepository;
import com.orderservice.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final JpaOrderRepository jpaOrderRepository;

//    @Override
//    public Page<Order> getOrderList(Long userId, Pageable pageable) {
//        return  jpaOrderRepository.findOrdersWithProductsList(userId,pageable);
//    }
//
//    @Override
//    public Order getOrderProductsList(Long orderId) {
//        return jpaOrderRepository.findOrderWithProduct(orderId);
//    }

    @Override
    public void save(Order order) {
        jpaOrderRepository.save(order);
    }

    @Override
    public Optional<Order> findByOrderId(Long orderId) {
        return jpaOrderRepository.findById(orderId);
    }

    @Override
    public List<Order> findByStatus(OrderStatus status, LocalDateTime date){
        return jpaOrderRepository.findByStatusAndUpdatedAtBefore(status,date);
    }


}
