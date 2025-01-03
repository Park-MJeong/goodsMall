package com.hanghae.productservice.domain;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findProductById(Long id);
    List<Product> getProductList(String search, Long cursor, Pageable pageable);
    void save(Product product);

}