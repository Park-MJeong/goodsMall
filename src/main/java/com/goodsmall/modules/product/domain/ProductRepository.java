package com.goodsmall.modules.product.domain;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findProductById(Long id);
    List<Product> getProductList(String search, Long cursor, Pageable pageable);
    Optional<Product> getProduct(Long id);
    void save(Product product);

}