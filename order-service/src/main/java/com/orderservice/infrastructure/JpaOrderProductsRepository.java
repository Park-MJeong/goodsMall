package com.orderservice.infrastructure;


import com.orderservice.domain.entity.Order;
import com.orderservice.domain.entity.OrderProducts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaOrderProductsRepository extends JpaRepository<OrderProducts, Long> {
    List<OrderProducts> findByOrder(Order order);

}
