package com.hanghae.productservice.service;

import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheableProductService {
    private final ProductRepository repository;

    public CacheableProductService(ProductRepository repository) {
        this.repository = repository;
    }

    /**
     * 공통 :제품정보 제공 (상태 예외처리 없이 전달)
     */
    @Cacheable(value = "productDetails", key = "#id")
    public Product getProductAll(Long id){
        return repository.findProductById(id).orElseThrow(
                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

}
