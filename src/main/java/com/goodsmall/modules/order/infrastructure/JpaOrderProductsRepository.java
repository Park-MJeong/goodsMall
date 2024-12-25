package com.goodsmall.modules.order.infrastructure;

import com.goodsmall.modules.order.domain.entity.Order;
import com.goodsmall.modules.order.domain.entity.OrderProducts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaOrderProductsRepository extends JpaRepository<OrderProducts, Long> {
    List<OrderProducts> findByOrder(Order order);

}
