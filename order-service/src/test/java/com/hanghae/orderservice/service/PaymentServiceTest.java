package com.hanghae.orderservice.service;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.orderservice.domain.PaymentRepository;
import com.hanghae.orderservice.domain.entity.Order;
import com.hanghae.orderservice.domain.entity.OrderProducts;
import com.hanghae.orderservice.domain.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private OrderService orderService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private RedisTemplate<String, Integer> redisTemplate;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private ValueOperations<String, Integer> valueOperations;
    @Mock
    private RLock rLock;

    @InjectMocks
    private PaymentService paymentService;

    private Order testOrder;
    private Payment testPayment;
    private List<OrderProducts> testOrderProducts;

    @BeforeEach
    void setUp() {
        testOrder = Order.builder()
                .id(1L)
                .build();

        testPayment = new Payment(testOrder.getId());

        OrderProducts product1 = OrderProducts.builder()
                .id(1L)
                .order(testOrder)
                .productId(1L)
                .quantity(1)
                .price(new BigDecimal("10000"))
                .build();
        OrderProducts product2 = OrderProducts.builder()
                .id(2L)
                .order(testOrder)
                .productId(1L)
                .quantity(1)
                .price(new BigDecimal("10000"))
                .build();

        testOrderProducts = Arrays.asList(product1);
    }

    @Test
    @DisplayName("1000개 동시 결제 요청 처리 테스트")
    void handleMultipleConcurrentPayments() throws Exception {
        // Given
        int numberOfThreads = 1000;
        int totalRequests = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(totalRequests);

        // Redis Template 설정
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.decrement(anyString(), anyLong())).thenReturn(10L);

        // 필요한 stubbing 설정
        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);
        when(orderService.getOrderProductList(any(Order.class))).thenReturn(testOrderProducts);

        // Lock 관련 stubbing
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);

        // Payment 저장 관련 stubbing
        doAnswer(invocation -> invocation.getArgument(0))
                .when(paymentRepository)
                .save(any(Payment.class));

        try {
            // When
            List<Future<ApiResponse<?>>> futures = new ArrayList<>();

            Callable<ApiResponse<?>> task = () -> {
                try {
                    return paymentService.createPayment(1L);
                } finally {
                    latch.countDown();
                }
            };

            for (int i = 0; i < totalRequests; i++) {
                futures.add(executorService.submit(task));
            }

            // 모든 요청이 완료될 때까지 대기
            boolean completed = latch.await(30, TimeUnit.SECONDS);
            assertThat(completed).isTrue().as("모든 요청이 30초 내에 완료되어야 합니다");

            // Then
            for (Future<ApiResponse<?>> future : futures) {
                ApiResponse<?> response = future.get(1, TimeUnit.SECONDS);
                assertThat(response.getSuccessMessage()).isEqualTo("정상적으로 처리되었습니다.");
            }

            // 검증
            verify(paymentRepository, times(totalRequests * 2)).save(any());
            verify(orderService, times(totalRequests)).orderComplete(anyLong());
            verify(redissonClient, times(totalRequests)).getLock(anyString());
            verify(rLock, times(totalRequests)).tryLock(
                    eq(10L), eq(2L), eq(TimeUnit.SECONDS)
            );
            verify(rLock, atLeastOnce()).isHeldByCurrentThread();
            verify(rLock, times(totalRequests)).unlock();

            // 재고 감소 검증
            for (OrderProducts product : testOrderProducts) {
                verify(valueOperations, times(totalRequests)).decrement(
                        eq("product:stock:" + product.getProductId()),
                        eq((long) product.getQuantity())
                );
            }

        } finally {
            executorService.shutdown();
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        }
    }

    @Test
    @DisplayName("재고 제한 테스트 - 100개 재고로 100명만 구매 가능")
    void handleLimitedStock() throws Exception {
        // Given
        int numberOfThreads = 200;      // 스레드 풀 크기
        int totalRequests = 10000;      // 10000명 시도
        int stockLimit = 100;           // 재고 100개
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(totalRequests);

        // 상품의 재고 관리
        Map<String, AtomicInteger> stockMap = new HashMap<>();
        stockMap.put("product:stock:1", new AtomicInteger(stockLimit));  // 100개 재고

        // Redis Template 설정
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 재고 감소 로직 모킹
        when(valueOperations.decrement(anyString(), anyLong())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Long quantity = invocation.getArgument(1);

            AtomicInteger stock = stockMap.get(key);
            if (stock == null) {
                return -1L;
            }

            // 동시성 환경에서의 재고 감소 시뮬레이션
            int remainingStock = stock.addAndGet(-quantity.intValue());
            if (remainingStock < 0) {
                stock.addAndGet(quantity.intValue()); // 재고 복구
                return -1L;
            }
            return (long) remainingStock;
        });

        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);
        when(orderService.getOrderProductList(any(Order.class))).thenReturn(testOrderProducts);
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);

        doAnswer(invocation -> invocation.getArgument(0))
                .when(paymentRepository)
                .save(any(Payment.class));

        try {
            // When
            List<Future<ApiResponse<?>>> futures = new ArrayList<>();

            for (int i = 0; i < totalRequests; i++) {
                int finalI = i;
                futures.add(executorService.submit(() -> {
                    try {
                        return paymentService.createPayment(finalI + 1L);  // 각각 다른 주문 ID 사용
                    } finally {
                        latch.countDown();
                    }
                }));
            }

            // 모든 요청 완료 대기 (최대 1분)
            boolean completed = latch.await(1, TimeUnit.MINUTES);
            assertThat(completed).isTrue()
                    .withFailMessage("모든 요청이 1분 내에 완료되어야 합니다");

            // Then
            int successCount = 0;
            int failCount = 0;
            List<String> successOrders = new ArrayList<>();
            List<String> failedOrders = new ArrayList<>();

            for (int i = 0; i < futures.size(); i++) {
                ApiResponse<?> response = futures.get(i).get(1, TimeUnit.SECONDS);
                if (response.getError() == null) {
                    successCount++;
                    successOrders.add("Order " + (i + 1) + ": Success");
                } else {
                    failCount++;
                    failedOrders.add("Order " + (i + 1) + ": Failed - " + response.getError().getMessage());
                }
            }

            // 결과 출력
            System.out.println("\n=== 테스트 결과 ===");
            System.out.println("총 요청 수: " + totalRequests);
            System.out.println("성공 횟수: " + successCount);
            System.out.println("실패 횟수: " + failCount);
            System.out.println("남은 재고: " + stockMap.get("product:stock:1").get());
            System.out.println("\n처음 10개 성공 주문:");
            successOrders.stream().limit(10).forEach(System.out::println);
            System.out.println("\n처음 10개 실패 주문:");
            failedOrders.stream().limit(10).forEach(System.out::println);

            // 검증
            assertThat(successCount).isEqualTo(stockLimit)
                    .withFailMessage("성공한 요청 수가 재고 수량과 일치해야 합니다");
            assertThat(failCount).isEqualTo(totalRequests - stockLimit)
                    .withFailMessage("실패한 요청 수가 (전체 요청 - 재고 수량)과 일치해야 합니다");
            assertThat(stockMap.get("product:stock:1").get()).isEqualTo(0)
                    .withFailMessage("재고가 모두 소진되어야 합니다");

            // 메서드 호출 횟수 검증
            verify(valueOperations, atLeast(totalRequests)).decrement(anyString(), anyLong());
            verify(orderService, times(stockLimit)).orderComplete(anyLong());
            verify(orderService, times(totalRequests - stockLimit)).failOrder(anyLong());

        } finally {
            executorService.shutdown();
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        }
    }
}