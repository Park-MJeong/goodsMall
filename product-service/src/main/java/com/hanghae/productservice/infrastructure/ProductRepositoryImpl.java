package com.hanghae.productservice.infrastructure;


import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;

    @Override
    public Optional<Product> findProductById(Long id) {
        return jpaProductRepository.findById(id);
    }

    @Override
    public List<Product> getProductList(String search, Long cursor, Pageable pageable) {
        return jpaProductRepository.findOrderByOpenDateDesc(search, cursor, pageable);
    }

    @Override
    public void save(Product product) {
        jpaProductRepository.save(product);
    }

    @Override
    public List<Product> openingTodayProducts(LocalDateTime start,LocalDateTime end) {
        return jpaProductRepository.openingTodayProducts(start,end);
    }


}
