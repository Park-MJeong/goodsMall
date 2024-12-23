package com.goodsmall.modules.order.infrastructure;

import com.goodsmall.modules.order.domain.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface JpaOrderRepository extends JpaRepository<Order, Long> {
    @Query("""
        SELECT o FROM Order o
        LEFT JOIN FETCH o.orderProducts op
        LEFT JOIN FETCH op.product p
        WHERE o.user.id = :userId
        ORDER BY o.createdAt DESC,o.id desc
    """)
    Page<Order> findOrdersWithProducts(@Param("userId") Long userId, Pageable pageable);

    @Query(
    """
    SELECT o
    FROM Order o
    LEFT JOIN FETCH o.orderProducts op
    LEFT JOIN FETCH op.product p
    WHERE o.id = :orderId
    ORDER BY o.createdAt DESC,o.id desc
    """)
    Order findOrderById(@Param("orderId") Long orderId);



}
