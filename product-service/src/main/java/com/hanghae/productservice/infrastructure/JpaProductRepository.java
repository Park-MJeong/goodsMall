package com.hanghae.productservice.infrastructure;

import com.hanghae.productservice.domain.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
        AND p.status IN ('Pre-sale','On Sale')
        order by p.openDate ASC,p.id ASC
        """)
    List<Product> findOrderByOpenDateDesc(String keyword, Long cursor, Pageable pageable);

//    @Query("select p from Product p where p.id= :productId AND (p.status = 'Pre-sale' OR p.status = 'On Sale')")
//    Optional<Product> findProduct(long productId);
    @Query(
            """
            SELECT p.id,p.quantity
            FROM Product p
            WHERE 1=1
            AND p.openDate >=:start AND p.openDate <:end
            order by p.openDate ASC,p.id ASC
            """)
    List<Product> openingTodayProducts(LocalDateTime start,LocalDateTime end);

    }
