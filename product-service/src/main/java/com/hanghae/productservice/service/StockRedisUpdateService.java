package com.hanghae.productservice.service;

import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockRedisUpdateService {
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Integer> redisTemplate;
    private static final String REDIS_STOCK_KEY = "product:stock:";

    protected StockRedisUpdateService(ProductRepository productRepository, RedisTemplate<String, Integer> redisTemplate) {
        this.productRepository = productRepository;
        this.redisTemplate = redisTemplate;
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
