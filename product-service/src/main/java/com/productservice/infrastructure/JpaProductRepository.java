package com.productservice.infrastructure;

import com.productservice.domain.Product;
import com.productservice.dto.ProductDto;
import com.productservice.dto.SliceProductDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface JpaProductRepository extends JpaRepository<Product, Long> {
    @Query(
    """
    SELECT new com.productservice.dto.SliceProductDto(p.id,p.productName,p.productPrice,p.openDate,p.status)
    FROM Product p
    WHERE 1=1
    AND (p.productName LIKE %:keyword% OR :keyword IS NULL)
    AND p.id >:cursor
    AND (p.status = 'Pre-sale' OR p.status = 'On Sale')
    order by p.openDate ASC,p.id ASC
    """)
    List<SliceProductDto> findOrderByOpenDateDesc(String keyword, Long cursor, Pageable pageable);

    @Query(
    """
    SELECT new com.productservice.dto.ProductDto(
        p.productName, p.description, p.productPrice, p.openDate, p.status)
    FROM Product p
    WHERE p.id = :id
    AND (p.status = 'Pre-sale' OR p.status = 'On Sale')
    """
    )
    Optional<ProductDto> dtoFindById(long id);

//    @Lock(LockModeType.OPTIMISTIC)
    @Query("select p from Product p where p.id= :productId AND (p.status = 'Pre-sale' OR p.status = 'On Sale')")
    Optional<Product> findProduct(long productId);

}
