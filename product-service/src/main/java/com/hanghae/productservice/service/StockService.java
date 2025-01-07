package com.hanghae.productservice.service;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductRepository;
import com.hanghae.productservice.dto.StockProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j(topic = "재고정보")
@Service
public class StockService {
    private final ProductRepository productRepository;
    private static final String REDIS_STOCK_KEY = "product:stock:";
    private final RedisTemplate<String,Integer> redisTemplate;

    public StockService(ProductRepository productRepository, RedisTemplate<String,Integer> redisTemplate) {
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 해당 제품 재고 조회
     * */
    public Long getStock(Long productId){
        String key = REDIS_STOCK_KEY + productId;
//        레디스에서 재고 차감 (재고 예약)
//        get보다 incre,decre 자체가 원자성을 보장해줌
        Long stock = redisTemplate.opsForValue().decrement(key);

        if (stock==null || stock <0) {
            redisTemplate.opsForValue().increment(key);
            throw new BusinessException(ErrorCode.PRODUCT_SOLD_OUT);
        }
        return stock;

    }



//        당일 한정판매 제품리스트 재고 조회
    public ResponseEntity<ApiResponse<?>> getProductStockList() {
        List<StockProductDto> stockProductDtoList = new ArrayList<>();
        Set<String> redisKeys = redisTemplate.keys(REDIS_STOCK_KEY + "*");


        assert redisKeys != null;
        for (String redisKey : redisKeys) {
            if ( redisKey == null ||  redisKey.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("당일 한정판매 제품이 없습니다."));
            }

            Long productId = Long.parseLong(redisKey.replace(REDIS_STOCK_KEY, ""));
            int stock = redisTemplate.opsForValue().get(redisKey);

            // 제품 정보와 재고를 StockProductDto에 저장
            stockProductDtoList.add(new StockProductDto(
                    productId,
                    stock
            ));
        }

        return ResponseEntity.ok(ApiResponse.success(stockProductDtoList));
    }

    //    당일에 오픈예정인 한정판매물건들 재고 파악
    @Scheduled(cron = "0 0 0 * * *")
    public void updateStock(){
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();

        List<Product> openProducts = productRepository.openingTodayProducts(start,end);
        if(openProducts.isEmpty()){
            return;
        }
        for (Product openProduct : openProducts) {
//            ttl 하루로 지정
            redisTemplate.opsForValue().set(REDIS_STOCK_KEY + openProduct.getId(),
                    openProduct.getQuantity(), Duration.ofDays(1));
        }
    }

}
