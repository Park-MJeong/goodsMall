package com.hanghae.orderservice.client;

import com.hanghae.common.dto.ProductResponseDto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/api/products/information/{productId}")
    ProductResponseDto information(@PathVariable("productId")Long productId);

    @PostMapping("/api/products/decreaseStock")
    void decreaseStock(@RequestParam("productId")Long productId,@RequestParam("quantity")Integer quantity);

    @PostMapping("/api/products/increaseStock")
    void increaseStock(@RequestParam("productId")Long productId,@RequestParam("quantity")Integer quantity);
}
