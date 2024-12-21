package com.goodsmall.modules.product.domain;

import com.goodsmall.modules.product.dto.SliceProductDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepository {
    List<SliceProductDto> getProductList(String search, int cursor, Pageable pageable);
}
