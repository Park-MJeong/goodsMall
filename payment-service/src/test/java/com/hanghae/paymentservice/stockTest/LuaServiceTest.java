package com.hanghae.paymentservice.stockTest;

import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.common.kafka.OrderRequestDto;
import com.hanghae.common.util.RedisKeyUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LuaServiceTest {

    @Autowired
    private LuaService luaService;


    private static final Long PRODUCT_ID = 1L;
    private static final int INITIAL_STOCK = 5;
    private static final String STOCK_KEY = RedisKeyUtil.getStockKey(PRODUCT_ID);
    private static int cnt =0;


    @Test
    void initTest() throws InterruptedException {
        int numberOfUsers = 100; // 동시 주문 사용자 수
        int threadPoolSize = 100; // 스레드풀 크기 설정
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        CountDownLatch latch = new CountDownLatch(numberOfUsers);


        for (int i = 0; i < numberOfUsers; i++) {
            OrderRequestDto dto = new OrderRequestDto(1L, 1); // 상품 ID 1번, 수량 1개
            List<OrderRequestDto> orderList = List.of(dto);
            OrderEvent orderEvent = new OrderEvent((long) i, BigDecimal.valueOf(100.00),orderList);

            executorService.submit(() -> {
                try {
                    Thread.sleep((long) (Math.random() * 100)); // 0~100ms 랜덤 지연 추가
                    luaService.initPayment(orderEvent); // 각 사용자 1개씩 주문

                } catch (Exception e) {
                    System.out.println("예외 발생: " + e.getMessage());
                    cnt+=1;
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 요청이 완료될 때까지 대기
        latch.await();
        executorService.shutdown();


        // 주문 성공 수는 초기 재고 수량을 초과하면 안 됨
        Assertions.assertEquals(INITIAL_STOCK, numberOfUsers-cnt, "재고수량과 성공주문수 같아야 함");
        Assertions.assertEquals(numberOfUsers - INITIAL_STOCK, cnt, "전체주문수에서 재고수량을 뺀것은 실패해야함");
    }



}





