package com.goodsmall.modules.order.infrastructure;

import com.goodsmall.modules.order.domain.entity.OrderProducts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaOrderProductsRepository extends JpaRepository<OrderProducts, Long> {
    @Query("""
        SELECT DISTINCT op
        FROM OrderProducts op
        LEFT JOIN FETCH op.product p
        WHERE op.order.id = :orderId
        ORDER BY op.quantity,op.id desc
        """)
    Page<OrderProducts> findOrdersWithProducts(Long orderId,Pageable pageable);
}
