package com.goodsmall.modules.product.infrastructure;

import com.goodsmall.modules.product.domain.Product;
import com.goodsmall.modules.product.domain.ProductRepository;
import com.goodsmall.modules.product.dto.ProductDto;
import com.goodsmall.modules.product.dto.SliceProductDto;
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
    public Optional<Product> findById(Long id) {
        return jpaProductRepository.findById(id);
    }

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
