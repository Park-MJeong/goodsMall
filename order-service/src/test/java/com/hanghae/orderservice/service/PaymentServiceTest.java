//package com.hanghae.orderservice.service;
//
//import com.hanghae.common.api.ApiResponse;
//import com.hanghae.orderservice.domain.PaymentRepository;
//import com.hanghae.orderservice.domain.entity.Order;
//import com.hanghae.orderservice.domain.entity.OrderProducts;
//import com.hanghae.orderservice.domain.entity.Payment;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.Executors;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//class PaymentServiceTest {
//    @Mock
//    private OrderService orderService;
//    @Mock
//    private PaymentRepository paymentRepository;
//    @Mock
//    private RedisTemplate<String, Integer> redisTemplate;
//    @Mock
//    private RedissonClient redissonClient;
//    @Mock
//    private ValueOperations<String, Integer> valueOperations;
//    @Mock
//    private RLock rLock;
//
//    private PaymentService paymentService;
//
//    private Order testOrder;
//    private Payment testPayment;
//    private List<OrderProducts> testOrderProducts;
//
//    // 상수 정의
//    private static final long USER_ID=1L;
//    private static final long ORDER_ID = 1L;
//    private static final long PRODUCT_ID = 100L;
//    private static final int QUANTITY = 2;
//    private static final String REDIS_STOCK_KEY = "product:stock:";
//    private static final String key = REDIS_STOCK_KEY + PRODUCT_ID;
//
//    @BeforeEach
//    void setUp() {
//        // Mock 초기화
//        MockitoAnnotations.openMocks(this);
//
//        // ValueOperations Mock 설정 : 테스트환경에서 redis값 다룸
//        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//        when(redissonClient.getLock(anyString())).thenReturn(rLock);
//
//        paymentService = new PaymentService(
//                orderService,
//                paymentRepository,
//                redisTemplate,
//                redissonClient
//        );
//
//        // 테스트 데이터 설정
//        testOrder = Order.builder()
//                .id(1L)
//                .build();
//
//        testPayment = new Payment(1L);
//
//        OrderProducts product1 = OrderProducts.builder()
//                .id(1L)
//                .order(testOrder)
//                .productId(1L)
//                .quantity(1)
//                .price(new BigDecimal("10000"))
//                .build();
//
//        testOrderProducts = Collections.singletonList(product1);
//    }
//
//    @Test
//    @DisplayName("레디스재고 감소")
//    public void testDecreaseStock_Success() {
//        // Given
//        when(valueOperations.decrement(key,QUANTITY)).thenReturn(10L);
//
//        // When
//        boolean result = paymentService.decreaseStock(PRODUCT_ID, QUANTITY);
//
//        // Then
//        assertThat(result).isTrue();
//        verify(valueOperations).decrement(key, QUANTITY);
//    }
//
//    @Test
//    @DisplayName("레디스재고 감소 후 증가")
//    public void testDecreaseStock_Failure() {
//        // Given
//        when(valueOperations.decrement(eq(key), eq((long) QUANTITY))).thenReturn(-1L);
//
//        // When
//        boolean result = paymentService.decreaseStock(PRODUCT_ID, QUANTITY);
//
//        // Then
////        결과값 & 메소드 호출확인
//        assertThat(result).isFalse();
//        verify(valueOperations).decrement(key, QUANTITY);
//        verify(valueOperations).increment(key, QUANTITY);
//    }
//
//    @Test
//    @DisplayName("10000명 동시 주문 - 재고100개")
//    void testConcurrentOrders() throws InterruptedException {
//        // Given
//        int totalUsers = 10000;
//        int threadPoolSize = 100;
//        int stockQuantity = 100;
//        AtomicInteger cnt= new AtomicInteger();
//        AtomicInteger fail= new AtomicInteger();
//
//        AtomicInteger currentStock = new AtomicInteger(stockQuantity); //재고
//
//        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
//        CountDownLatch latch = new CountDownLatch(totalUsers);
//
//        // Mock 설정
//        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//        when(redissonClient.getLock(anyString())).thenReturn(rLock);
//        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
//        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);
////        when(orderService.getOrderProductList(any(Order.class))).thenReturn(testOrderProducts);
//
//        // 재고 감소 로직 모킹 - 동기화 처리 추가
//        when(valueOperations.decrement(anyString(), anyLong())).thenAnswer(invocation -> {
//                int stock = currentStock.get();
//                if (stock <= 0) {
//                    return -1L;
//                }
//                return (long) currentStock.decrementAndGet();
//        });
//
//        // When
//        for (int i = 0; i < totalUsers; i++) {
//            final long orderId = i + 1;
//            executorService.submit(() -> {
//                try {
//                    ApiResponse<?> response = paymentService.createPayment(orderId,USER_ID);
//
//                    if (response.getError() == null) {
//                        cnt.getAndIncrement();
//
//                    } else {
//                        fail.getAndIncrement();
//                    }
//                } catch (Exception e) {
//                    fail.getAndIncrement();
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        // Then
//        latch.await(10, TimeUnit.SECONDS);
//        executorService.shutdown();
//
//        // 결과 출력
//        System.out.println("\n=== 동시성 주문 테스트 결과 ===");
//        System.out.println("총 요청 수: " + totalUsers);
//        System.out.println("초기 재고: " + stockQuantity);
//        System.out.println("성공 주문 수: " + cnt);
//        System.out.println("실패 주문 수: " +fail);
//
//
//        // 검증
//        assertThat(cnt.get()).isEqualTo(stockQuantity)
//                .withFailMessage("성공 주문 수가 초기 재고량과 일치해야 합니다");
//        assertThat(fail.get()).isEqualTo(totalUsers - stockQuantity)
//                .withFailMessage("실패한 주문 수가 (전체 주문 - 재고량)과 일치해야 합니다");
//        assertThat(currentStock.get()).isEqualTo(0)
//                .withFailMessage("재고가 모두 소진되어야 합니다");
//
//    }
//
//
//    @Test
//    @DisplayName("10000명 동시 주문 순서추적")
//    void ConcurrentOrders() throws InterruptedException {
//        // Given
//        int totalUsers = 10000;
//        int threadPoolSize = 100;
//        int stockQuantity = 100;
//        AtomicInteger currentStock = new AtomicInteger(stockQuantity); //재고
//        AtomicInteger processCount = new AtomicInteger(0); // 처리 순서 추적용
//
//        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
//        CountDownLatch latch = new CountDownLatch(totalUsers);
//        List<String> successOrders = Collections.synchronizedList(new ArrayList<>());
//        List<String> failedOrders = Collections.synchronizedList(new ArrayList<>());
//
//        // Mock 설정
//        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
//        when(orderService.getOrderById(anyLong())).thenReturn(testOrder);
////        when(orderService.getOrderProductList(any(Order.class))).thenReturn(testOrderProducts);
//
//        // 재고 감소 로직 모킹 - 동기화 처리 추가
//        when(valueOperations.decrement(anyString(), anyLong())).thenAnswer(invocation -> {
//                int stock = currentStock.get();
//                if (stock <= 0) {
//                    return -1L;
//                }
//                return (long) currentStock.decrementAndGet();
//        });
//
////        Payment mockPayment = mock(Payment.class);
////        when(mockPayment.getOrderId()).thenReturn(1L);
////        when(paymentRepository.findByOrderId(anyLong())).thenReturn(mockPayment);
//
//        // When
//        for (int i = 0; i < totalUsers; i++) {
//            final long orderId = i + 1;
//            executorService.submit(() -> {
//                try {
//                    Thread.sleep((long) (Math.random() * 100));
////                    ApiResponse<?> response = paymentService.createPayment(orderId,USER_ID);
//
//                    int orderNumber = processCount.incrementAndGet();
//
//                    if (response.getError() == null) {
//
//                            successOrders.add(String.format("주문 #%d - 성공 (처리순서: %d, 남은재고: %d)",
//                            orderId, orderNumber, currentStock.get()));
//
//                    } else {
//                        failedOrders.add(String.format("주문 #%d - 실패 (처리순서: %d, 재고부족)",
//                                orderId, orderNumber));
//                    }
//                } catch (Exception e) {
//                    failedOrders.add(String.format("주문 #%d - 실패 (%s)", orderId, e.getMessage()));
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        // Then
//        latch.await(10, TimeUnit.SECONDS);
//        executorService.shutdown();
//
//        // 결과 출력
//        System.out.println("\n=== 동시성 주문 테스트 결과 ===");
//        System.out.println("총 요청 수: " + totalUsers);
//        System.out.println("초기 재고: " + stockQuantity);
//        System.out.println("성공 주문 수: " + successOrders.size());
//        System.out.println("실패 주문 수: " + failedOrders.size());
//
//        System.out.println("\n=== 성공한 주문 목록 ===");
//        successOrders.forEach(System.out::println);
//
//        System.out.println("\n=== 실패한 주문 목록 (첫 10개) ===");
//        failedOrders.stream().limit(10).forEach(System.out::println);
//
//        // 검증
//        assertThat(successOrders).hasSize(stockQuantity)
//                .withFailMessage("성공 주문 수가 초기 재고량과 일치해야 합니다");
//        assertThat(failedOrders).hasSize(totalUsers - stockQuantity)
//                .withFailMessage("실패한 주문 수가 (전체 주문 - 재고량)과 일치해야 합니다");
//        assertThat(currentStock.get()).isEqualTo(0)
//                .withFailMessage("재고가 모두 소진되어야 합니다");
//
//    }
//}