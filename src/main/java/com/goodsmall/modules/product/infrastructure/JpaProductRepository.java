package com.goodsmall.modules.product.infrastructure;

import com.goodsmall.modules.product.domain.Product;
import com.goodsmall.modules.product.dto.ProductDto;
import com.goodsmall.modules.product.dto.SliceProductDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface JpaProductRepository extends JpaRepository<Product, Long> {
    @Query(
    """
    SELECT new com.goodsmall.modules.product.dto.SliceProductDto(p.id,p.productName,p.productPrice,p.image,p.openDate,p.status)
    FROM Product p
    WHERE 1=1
    AND (p.productName LIKE %:keyword% OR :keyword IS NULL)
    AND p.id >:cursor
    AND (p.status = 'Pre-sale' OR p.status = 'On Sale')
    order by p.openDate ASC,p.id ASC
    """)
    List<SliceProductDto> findOrderByOpenDateDesc(String keyword, int cursor, Pageable pageable);

    @Query(
    """
    SELECT new com.goodsmall.modules.product.dto.ProductDto(
        p.productName, p.description, p.image, p.productPrice, p.openDate, p.status)
    FROM Product p
    WHERE p.id = :id
    AND (p.status = 'Pre-sale' OR p.status = 'On Sale')
    """
    )
    Optional<ProductDto> dtofindById(long id);
}
