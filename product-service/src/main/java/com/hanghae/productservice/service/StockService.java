package com.hanghae.productservice.service;

import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductRepository;
import com.hanghae.productservice.dto.StockProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.hanghae.common.util.RedisKeyUtil.getStockKey;

@Slf4j(topic = "상품 재고정보")
@RequiredArgsConstructor
@Service
public class StockService {
    private final CacheableProductService cacheableProductService;
    private final ProductRepository productRepository;
    private final RedisTemplate<String,Integer> redisTemplate;

    /**
     * 해당 제품 재고 조회
     * */
    @Transactional(readOnly = true)
    public StockProductDto getStock(Long productId){
        return StockProductDto.builder()
                .productId(productId)
                .stock(getRedisStock(productId))
                .build();
    }

//    레디스에서 재고 조회
    private Integer getRedisStock(Long productId){
        String key = getStockKey(productId);
        Integer stock = redisTemplate.opsForValue().get(key);
        if(stock == null){

            return getStockFromDbAndCache(productId);
        }
        return stock;
    }

//    없으면 db에서 재고 조회, 레디스에 값 저장
    private Integer getStockFromDbAndCache(Long productId){
        Product product = cacheableProductService.getProductAll(productId);
        String key = getStockKey(productId);
        redisTemplate.opsForValue().set(key,product.getQuantity(),Duration.ofDays(1));
        return product.getQuantity();
    }




    /**
     * 당일에 오픈예정인 한정판매제품 재고 캐싱
     * 자정에 오픈하는 제품 없다고 가정함
     * */
    //    당일에 오픈예정인 한정판매물건들 재고 저장
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
            redisTemplate.opsForValue().set(getStockKey(openProduct.getId()),
                    openProduct.getQuantity(), Duration.ofDays(1));
        }
    }

//    // Redis와 DB 재고 동기화 (주기적으로 실행)
//    @Scheduled(fixedRate = 300000) // 5분마다 실행
//    public void synchronizeStock() {
//        List<Product> products = productRepository.findAll();
//        for (Product product : products) {
//            String stockKey = STOCK_KEY_PREFIX + product.getId();
//            redisTemplate.opsForValue().set(stockKey,product.getQuantity());
//        }
//    }

}
