package com.goodsmall.modules.product.infrastructure;

import com.goodsmall.modules.product.domain.entity.ShowProduct;
import com.goodsmall.modules.product.dto.SliceShowProductDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface JpaProductRepository extends JpaRepository<ShowProduct, Long> {
    @Query(
    """
    SELECT new com.goodsmall.modules.product.dto.SliceShowProductDto(sp.id,sp.product.id,sp.product.productName,sp.productPrice,sp.product.image,sp.openDate)
    FROM ShowProduct sp
    WHERE 1=1
    AND (sp.product.productName LIKE %:keyword% OR :keyword IS NULL)
    AND sp.id >:cursor
    order by sp.openDate ASC,sp.id ASC
    """)
    List<SliceShowProductDto> findOrderByOpenDateDesc(String keyword, int cursor, Pageable pageable);
}
