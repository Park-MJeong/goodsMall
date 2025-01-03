package com.hanghae.productservice.service;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.productservice.dto.StockProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j(topic = "재고정보")
@Service
public class StockService {
    private static final String REDIS_STOCK_KEY = "product:stock:";
    private final RedisTemplate<String,Integer> redisTemplate;
    private final ProductService productService;

    public StockService(RedisTemplate<String, Integer> redisTemplate, ProductService productService) {
        this.redisTemplate = redisTemplate;
        this.productService = productService;
    }

//    제품 재고 조회
    public ResponseEntity<ApiResponse<?>> getStock(Long productId){
        String key = REDIS_STOCK_KEY + productId;
        Integer stock = redisTemplate.opsForValue().get(key);
        StockProductDto stockProductDto;

//        해당 제품의 재고가없다면 mysql에서 가져와서 redis에 저장
        if(stock == null){
            stockProductDto = new StockProductDto(productService.getProductAll(productId));
            redisTemplate.opsForValue().set(key, stockProductDto.getStock(), Duration.ofDays(1));
        }else{
//            스케쥴러를 통해 저장되어있는 값 dto로 변환
            stockProductDto =new StockProductDto(productId,stock);
        }
        return ResponseEntity.ok(ApiResponse.success(stockProductDto));
    }

    //    당일 한정판매 제품리스트 재고 조회
    public ResponseEntity<ApiResponse<?>> getProductStockList() {
        List<StockProductDto> stockProductDtoList = new ArrayList<>();
        Set<String> redisKeys = redisTemplate.keys(REDIS_STOCK_KEY + "*");


        assert redisKeys != null;
        for (String redisKey : redisKeys) {
            if ( redisKey == null ||  redisKey.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("당일 한정판매 제품이 없습니다."));
            }

            Long productId = Long.parseLong(redisKey.replace(REDIS_STOCK_KEY, ""));
            Integer stock = redisTemplate.opsForValue().get(redisKey);

            // 제품 정보와 재고를 StockProductDto에 저장
            stockProductDtoList.add(new StockProductDto(
                    productId,
                    stock
            ));
        }

        return ResponseEntity.ok(ApiResponse.success(stockProductDtoList));
    }

}
