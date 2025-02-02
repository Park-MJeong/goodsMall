package com.hanghae.orderservice.infrastructure;


import com.hanghae.orderservice.domain.entity.Order;
import com.hanghae.orderservice.domain.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface JpaOrderRepository extends JpaRepository<Order, Long> {
    @Query("""
        SELECT o FROM Order o
        LEFT JOIN FETCH o.orderProducts op
        WHERE o.userId = :userId
        ORDER BY o.createdAt DESC,o.id desc
    """)
    Page<Order> findOrderList(@Param("userId") Long userId, Pageable pageable);

    @Query(
    """
    SELECT o
    FROM Order o
    LEFT JOIN FETCH o.orderProducts op
    WHERE o.id = :orderId
    ORDER BY o.createdAt DESC,o.id desc
    """)
    Order findOrderWithProduct(Long orderId);


    List<Order> findByStatusAndUpdatedAtBefore(OrderStatus status, LocalDateTime date);

    @Modifying
    @Query(
        """
        UPDATE Order o
        SET o.status = :status
        WHERE o.id = :orderId
        """)
    void orderStatusUpdating(@Param("status")OrderStatus status, @Param("orderId")Long orderId);
}
