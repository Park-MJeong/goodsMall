package com.hanghae.paymentservice.client;

import com.hanghae.common.config.FeignConfig;
import com.hanghae.paymentservice.client.dto.OrderProductStock;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(name = "order-service",configuration = FeignConfig.class)
public interface OrderClient {
    @GetMapping("/api/orders/orderProductStock/{orderId}")
    List<OrderProductStock> orderProductStock(@PathVariable("orderId") Long orderId);

}
