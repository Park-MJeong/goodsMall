package com.hanghae.productservice.service;

import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductRepository;
import com.hanghae.productservice.dto.StockProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    private StockService stockService;

    @Mock
    private CacheableProductService cacheableProductService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RedisTemplate<String,Integer> redisTemplate;
    @Mock
    private ValueOperations<String, Integer> valueOperations;

    private Product product;
    private Product product2;

    private static final long PRODUCT_ID = 1L;
    private static final int QUANTITY = 20;
    private static final String REDIS_STOCK_KEY = "product:stock:";
    private static final String key = REDIS_STOCK_KEY + PRODUCT_ID;

    @BeforeEach
    void setUp() {
       when(redisTemplate.opsForValue()).thenReturn(valueOperations);
       product = Product.builder()
               .id(PRODUCT_ID)
               .quantity(QUANTITY)
               .build();
       product2 = Product.builder()
               .id(2L)
               .quantity(QUANTITY)
               .build();
    }

    @Test
    @DisplayName("레디스에 저장된 재고있음")
    void testGetStock() {
//        when
        when(valueOperations.get(key)).thenReturn(QUANTITY);
//        given
        StockProductDto dto = stockService.getStock(PRODUCT_ID);

//        then
        assertNotNull(dto);
        assertEquals(PRODUCT_ID, dto.getProductId());
        assertEquals(QUANTITY, dto.getStock());
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    @DisplayName("레디스에 저장된 재고없음")
    void testGetDbStock() {
//        when
        when(valueOperations.get(key)).thenReturn(null);
        when(cacheableProductService.getProductAll(PRODUCT_ID)).thenReturn(product);

//        given
        StockProductDto dto = stockService.getStock(PRODUCT_ID);

//        then: 결과값
        assertNotNull(dto);
        assertEquals(PRODUCT_ID, dto.getProductId());
        assertEquals(QUANTITY, dto.getStock());
//        then: 메서드 호출횟수
        verify(redisTemplate, times(2)).opsForValue();
        verify(cacheableProductService, times(1)).getProductAll(PRODUCT_ID);
        verify(valueOperations, times(1)).get(key);
        verify(valueOperations, times(1)).set(key,QUANTITY, Duration.ofDays(1));
    }
    @Test
    @DisplayName("스케쥴러 메서드 잘 동작하는 지 확인")
    void testUpdateStock(){
//       when
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();

        List<Product> openProducts = Arrays.asList(product, product2);
        when(productRepository.openingTodayProducts(start, end)).thenReturn(openProducts);

//        given
        stockService.updateStock();
//        then
        verify(productRepository, times(1)).openingTodayProducts(start, end);
        verify(redisTemplate, times(2)).opsForValue();
        verify(valueOperations, times(1)).set(REDIS_STOCK_KEY+PRODUCT_ID,QUANTITY, Duration.ofDays(1));
        verify(valueOperations, times(1)).set(REDIS_STOCK_KEY+2L,QUANTITY, Duration.ofDays(1));

        verifyNoMoreInteractions(cacheableProductService);
    }

}