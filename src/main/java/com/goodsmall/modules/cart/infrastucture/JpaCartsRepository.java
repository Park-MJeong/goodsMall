package com.goodsmall.modules.cart.infrastucture;

import com.goodsmall.modules.cart.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaCartsRepository extends JpaRepository<Cart, Long>{
    @Query(
            """
            SELECT c
            FROM Cart c
            LEFT JOIN FETCH c.cartProducts cp
            LEFT JOIN FETCH cp.product p
            WHERE c.id = :cartId
            AND (p.status = 'Pre-sale' OR p.status = 'On Sale')
            ORDER BY p.openDate asc
            """)
    Cart findCartById(@Param("cartId") Long cartId);

}
