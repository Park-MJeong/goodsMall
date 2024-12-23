package com.goodsmall.modules.order.infrastructure;

import com.goodsmall.modules.order.domain.entity.OrderProducts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaOrderProductsRepository extends JpaRepository<OrderProducts, Long> {

}
