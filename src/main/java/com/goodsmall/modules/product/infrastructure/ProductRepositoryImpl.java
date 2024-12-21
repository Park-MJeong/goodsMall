package com.goodsmall.modules.product.infrastructure;

import com.goodsmall.modules.product.domain.ProductRepository;
import com.goodsmall.modules.product.dto.SliceProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    @Override
    public List<SliceProductDto> getProductList(String search, int cursor, Pageable pageable) {
        return jpaProductRepository.findOrderByOpenDateDesc(search, cursor, pageable);
    }
}
