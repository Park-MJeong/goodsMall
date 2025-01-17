package com.hanghae.orderservice.client;


import com.hanghae.common.config.FeignConfig;
import com.hanghae.orderservice.client.dto.ProductIdAndQuantityDto;
import com.hanghae.orderservice.client.dto.ProductNameAndPriceDto;
import com.hanghae.orderservice.client.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "product-service",configuration = FeignConfig.class)
public interface ProductClient {
    @GetMapping("/api/products/information/{productId}")
    ProductResponseDto information(@PathVariable("productId")Long productId);

    @GetMapping("/api/products/productStatus/{productId}")
    ProductNameAndPriceDto availableProducts(@PathVariable Long productId);

//    @PostMapping("/api/products/decreaseStock")
//    void decreaseStock(@RequestParam("productId")Long productId,@RequestParam("quantity")Integer quantity);

    @PostMapping("/api/products/increaseStock")
    void increaseStock(@RequestBody ProductIdAndQuantityDto productIdAndQuantityDto);
}
