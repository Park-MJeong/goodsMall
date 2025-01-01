package com.hanghae.orderservice.domain;


import com.hanghae.orderservice.domain.entity.Order;
import com.hanghae.orderservice.domain.entity.OrderProducts;

import java.util.List;

public interface OrderProductRepository {
     void save(OrderProducts orderProducts);
     List<OrderProducts> findByOrder(Order order);


}
