package com.goodsmall.modules.order.domain;

import com.goodsmall.modules.order.domain.entity.OrderProducts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface OrderProductRepository {
    Page<OrderProducts> getOrderProductList(Long orderId, Pageable pageable);
}
