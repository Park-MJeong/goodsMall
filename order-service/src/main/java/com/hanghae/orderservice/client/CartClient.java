package com.hanghae.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "cart-service")
public interface CartClient {
}
