package com.orderservice.domain;


import com.orderservice.domain.entity.Order;
import com.orderservice.domain.entity.OrderProducts;

import java.util.List;

public interface OrderProductRepository {
     void save(OrderProducts orderProducts);
     List<OrderProducts> findByOrder(Order order);


}
