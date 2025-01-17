package com.hanghae.productservice.infrastructure;

import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface JpaProductRepository extends JpaRepository<Product, Long> {

    @Query(
            """

                    SELECT p
        FROM Product p
        WHERE 1=1
        AND (p.productName LIKE %:keyword% OR :keyword IS NULL)
        AND p.id >:cursor
        AND p.status IN (com.hanghae.productservice.domain.ProductStatus.PRE_SALE ,com.hanghae.productservice.domain.ProductStatus.ON_SALE)
        order by p.openDate ASC,p.id ASC
        """)
    List<Product> findOrderByOpenDateDesc(String keyword, Long cursor, Pageable pageable);

    @Query(
            """
            SELECT p.id,p.quantity
            FROM Product p
            WHERE 1=1
            AND p.openDate >=:start AND p.openDate <:end
            order by p.openDate ASC,p.id ASC
            """)
    List<Product> openingTodayProducts(LocalDateTime start ,LocalDateTime end);

    @Modifying
    @Query(
            """
            UPDATE Product p
            SET p.quantity = p.quantity+ :quantity,
                p.status = CASE WHEN p.quantity > 0 THEN 'ON_SALE' ELSE p.status END
            WHERE p.id = :productId
            """
    )
    void updateQuantityAndStatus(Long productId, Integer quantity);
    }
