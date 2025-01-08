package com.hanghae.productservice.controller;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.productservice.dto.StockProductDto;
import com.hanghae.productservice.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stock")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * 재고 API
     * 남은 재고 화면에 보여줌
     * */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> getStock(@PathVariable Long productId) {
        StockProductDto stock = stockService.getStock(productId);
        ApiResponse<?> response = ApiResponse.success(stock);
       return ResponseEntity.ok(response);
    }

}
