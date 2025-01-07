package com.hanghae.productservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductRepository;
import com.hanghae.productservice.dto.StockProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class StockRedisUpdateServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private RedisTemplate<String, Integer> redisTemplate;
    @Mock
    private ValueOperations<String, Integer> valueOperations;
    @Mock
    private ProductService productService;


    @InjectMocks
    private StockService stockService;

    private Product product1;
    private Product product2;


    @BeforeEach
    public void setup() {
        // 제품 데이터 초기화
        product1 = new Product(1L, "Product 1", "Description 1", BigDecimal.valueOf(100.00),
                LocalDateTime.now(), 100, "Pre-sale");
        product2 = new Product(2L, "Product 2", "Description 2", BigDecimal.valueOf(200.00),
                LocalDateTime.now(), 50, "On Sale");

        // mock을 사용하여 openingTodayProducts 메서드가 반환할 값 설정
        when(productRepository.openingTodayProducts(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(product1, product2));
//
        when(valueOperations.get("product:stock:" + product1.getId())).thenReturn(100);
        when(valueOperations.get("product:stock:" + product2.getId())).thenReturn(50);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

    }



    @Test
    public void testGetStock() {
        // 메서드 실행
        ResponseEntity<ApiResponse<?>> response = stockService.getStock(product1.getId());

        assertEquals(100, ((StockProductDto) response.getBody().getData()).getStock());

        // RedisTemplate의 `get` 호출 여부 확인
        verify(redisTemplate.opsForValue(), times(1)).get("product:stock:" + product1.getId());
    }

    @Test
    public void testGetStock_null() {
        // Redis에서 조회할 stock 값 없음 (null)
        when(valueOperations.get("product:stock:" + product1.getId())).thenReturn(null);
        when(productService.getProductAll(product1.getId())).thenReturn(product1);

        // 메서드 실행
        ResponseEntity<ApiResponse<?>> response = stockService.getStock(product1.getId());

        // Redis에 재고 정보가 제대로 저장되었는지 확인
        verify(redisTemplate.opsForValue(), times(1)).set("product:stock:" + product1.getId(), 100, Duration.ofDays(1));

        // 응답 데이터가 예상대로 저장되었는지 확인
        assertEquals(100, ((StockProductDto) response.getBody().getData()).getStock());
    }


    @Test
    public void testGetProductStockList() {
        // Redis에서 stock 값이 있는 경우 mock 설정
        when(redisTemplate.opsForValue().get("product:stock:" + product1.getId())).thenReturn(100);
        when(redisTemplate.opsForValue().get("product:stock:" + product2.getId())).thenReturn(50);

        // 메서드 실행
        ResponseEntity<ApiResponse<?>> response = stockService.getProductStockList();

        // 결과 검증
        if (response.getBody() != null && response.getBody().getData() != null) {
            @SuppressWarnings("unchecked")
            List<StockProductDto> stockProductDtoList = (List<StockProductDto>) response.getBody().getData();

            // 결과 검증
            assertEquals(2, stockProductDtoList.size()); // 두 개의 상품이 리스트에 있어야 함
            assertEquals(100, stockProductDtoList.get(0).getStock()); // 첫 번째 제품의 재고가 100이어야 함
            assertEquals(50, stockProductDtoList.get(1).getStock());  // 두 번째 제품의 재고가 50이어야 함
        } else {
            assertEquals("당일 한정판매 제품이 없습니다.", response.getBody().getData());
        }
    }

}


