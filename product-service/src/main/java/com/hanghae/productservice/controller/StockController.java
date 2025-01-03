package com.hanghae.productservice.controller;

import com.hanghae.common.api.ApiResponse;
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
     * 당일 한정판매 제품리스트 재고
     * */
    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> getProductStockList() {
        return stockService.getProductStockList();
    }
    /**
     * 해당 제품에 대한 재고
     * */
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<?>> getStock(@PathVariable Long productId) {
       return stockService.getStock(productId);
    }

}
