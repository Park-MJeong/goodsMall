package com.hanghae.cartservice.client;

import com.hanghae.cartservice.client.dto.ProductResponseDto;
import com.hanghae.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "product-service",configuration = FeignConfig.class)
public interface ProductClient {
    @GetMapping("/api/products/information/{productId}")
    ProductResponseDto information(@PathVariable("productId")Long productId);

    @GetMapping("/api/products/productQuantity/{productId}")
    ProductResponseDto getProductQuantity(@PathVariable("productId") Long productId);

}
