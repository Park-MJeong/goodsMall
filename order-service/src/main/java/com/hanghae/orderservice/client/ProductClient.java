package com.hanghae.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="product-service")
public interface ProductClient {
    @PostMapping("/api/products/decreaseStock")
    void decreaseStock(@RequestParam("productId")Long productId,@RequestParam("quantity")Integer quantity);
}
