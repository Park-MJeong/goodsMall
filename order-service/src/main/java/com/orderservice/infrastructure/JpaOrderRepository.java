package com.orderservice.infrastructure;

import com.orderservice.OrderStatus;
import com.orderservice.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface JpaOrderRepository extends JpaRepository<Order, Long> {
//    @Query("""
//        SELECT o FROM Order o
//        LEFT JOIN FETCH o.orderProducts op
//        LEFT JOIN FETCH op.product p
//        WHERE o.user.id = :userId
//        ORDER BY o.createdAt DESC,o.id desc
//    """)

//    @Query(
//    """
//    SELECT o
//    FROM Order o
//    LEFT JOIN FETCH o.orderProducts op
//    LEFT JOIN FETCH op.product p
//    WHERE o.id = :orderId
//    ORDER BY o.createdAt DESC,o.id desc
//    """)
//    Order findOrderWithProduct(Long orderId);


    List<Order> findByStatusAndUpdatedAtBefore(OrderStatus status, LocalDateTime date);
}
