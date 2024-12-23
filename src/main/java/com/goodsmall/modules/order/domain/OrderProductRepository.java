package com.goodsmall.modules.order.domain;

import com.goodsmall.modules.order.domain.entity.OrderProducts;


public interface OrderProductRepository {
    public void save(OrderProducts orderProducts);
}
