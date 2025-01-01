package com.hanghae.orderservice.infrastructure;


import com.hanghae.orderservice.domain.entity.Order;
import com.hanghae.orderservice.domain.entity.OrderProducts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaOrderProductsRepository extends JpaRepository<OrderProducts, Long> {
    List<OrderProducts> findByOrder(Order order);

}
