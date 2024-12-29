package com.productservice.infrastructure;


import com.productservice.domain.Product;
import com.productservice.domain.ProductRepository;
import com.productservice.dto.ProductDto;
import com.productservice.dto.SliceProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    @Override
    public List<SliceProductDto> getProductList(String search, Long cursor, Pageable pageable) {
        return jpaProductRepository.findOrderByOpenDateDesc(search, cursor, pageable);
    }

    @Override
    public Optional<ProductDto> getProductInformation(Long id) {
        return jpaProductRepository.dtoFindById(id);
    }
    @Override
    public Optional<Product> getProductAll(Long id) {
        return jpaProductRepository.findById(id);
    }
    @Override
    public Optional<Product> getProduct(Long id) {
        return jpaProductRepository.findProduct(id);
    }

    @Override
    public void save(Product product) {
        jpaProductRepository.save(product);
    }
}
