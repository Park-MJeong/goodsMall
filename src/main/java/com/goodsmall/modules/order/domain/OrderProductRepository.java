package com.goodsmall.modules.order.domain;

import com.goodsmall.modules.order.domain.entity.Order;
import com.goodsmall.modules.order.domain.entity.OrderProducts;

import java.util.List;


public interface OrderProductRepository {
     void save(OrderProducts orderProducts);
     List<OrderProducts> findByOrder(Order order);


}
