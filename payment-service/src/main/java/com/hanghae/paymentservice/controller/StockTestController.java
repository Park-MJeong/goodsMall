package com.hanghae.paymentservice.controller;


import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.paymentservice.stockTest.LuaService;
import com.hanghae.paymentservice.stockTest.RedissonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test/stock")
public class StockTestController {
    private final LuaService luaService;
    private final RedissonService redissonService;


    @PostMapping("/lua")
    public void LuaTest(@RequestBody OrderEvent  orderEvent) {
        luaService.initPayment(orderEvent);
    }
    @PostMapping("/rock")
    public void RockTest(@RequestBody OrderEvent orderEvent) {
        redissonService.initPayment(orderEvent);
    }
}
