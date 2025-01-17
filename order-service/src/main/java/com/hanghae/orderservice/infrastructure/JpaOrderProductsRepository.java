package com.hanghae.orderservice.infrastructure;


import com.hanghae.orderservice.domain.entity.Order;
import com.hanghae.orderservice.domain.entity.OrderProducts;
import com.hanghae.orderservice.dto.OrderProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaOrderProductsRepository extends JpaRepository<OrderProducts, Long> {
    @Query(
    """
    SELECT op.id,op.quantity FROM OrderProducts op WHERE op.order.id = :orderId
    """)
    List<OrderProductStock> findStockByOrderId(Long orderId);

}
