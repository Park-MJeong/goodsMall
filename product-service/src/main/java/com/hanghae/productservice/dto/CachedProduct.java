package com.hanghae.productservice.dto;

import com.hanghae.productservice.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CachedProduct {
    private Product product;
    private long timestamp;


}
