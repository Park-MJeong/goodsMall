package com.hanghae.productservice.dto;

import com.hanghae.productservice.domain.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CachedProduct {

    private final Product product;
    private final long timestamp;


}
