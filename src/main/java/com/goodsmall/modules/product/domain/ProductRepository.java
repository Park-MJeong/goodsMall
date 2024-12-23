package com.goodsmall.modules.product.domain;

import com.goodsmall.modules.product.dto.ProductDto;
import com.goodsmall.modules.product.dto.SliceProductDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<SliceProductDto> getProductList(String search, Long cursor, Pageable pageable);
    Optional<ProductDto> getProductInformation(Long id);
    Optional<Product> getProduct(Long id);
    void save(Product product);

    Optional<Product> getProductAll(Long id);
}