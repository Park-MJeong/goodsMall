package com.goodsmall.modules.order.domain;

import com.goodsmall.modules.order.domain.entity.OrderProducts;


public interface OrderProductRepository {
     void save(OrderProducts orderProducts);
}
