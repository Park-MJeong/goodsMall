package com.goodsmall.modules.order.infrastructure;

import com.goodsmall.modules.order.domain.OrderProductRepository;
import com.goodsmall.modules.order.domain.entity.OrderProducts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {
    private final JpaOrderProductsRepository repository;
    @Override
    public Slice<OrderProducts> getOrderProductList(Long orderid, Long cursor, Pageable pageable) {
        return repository.findByOrderIdOrderByCreatedAtDesc(orderid,cursor,pageable);
    }
}
