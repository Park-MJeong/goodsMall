package com.hanghae.orderservice.client;

import com.hanghae.common.config.FeignConfig;
import com.hanghae.orderservice.client.dto.CartProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "cart-service",configuration = FeignConfig.class)
public interface CartClient {
    @GetMapping("/api/carts/cartProductDto/{cartProductId}")
    CartProductDto cartProductDtoInfo(@PathVariable Long cartProductId);
    @GetMapping("/api/carts/deleteCartProductList")
    void deleteCartProductList(@RequestParam List<Long> cartProductId);

}
