package com.hanghae.orderservice.service;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.orderservice.domain.PaymentRepository;
import com.hanghae.orderservice.domain.entity.Order;
import com.hanghae.orderservice.domain.entity.OrderProducts;
import com.hanghae.orderservice.domain.entity.Payment;
import com.hanghae.orderservice.util.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j(topic = "결제화면")
@RequiredArgsConstructor
public class PaymentService {
    private final  OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final RedisTemplate<String,Integer> redisTemplate;
    private final RedissonClient redissonClient;
    private static final String REDIS_STOCK_KEY = "product:stock:";


    /**
     * 재고 파악 후 결제 테이블 생성
     * */
    public Payment initPayment(Long orderId,Long userId) {
        Order order = orderService.getOrderById(orderId);
//        1. 해당 주문과 결제 유저 일치 여부
        if(!order.getUserId().equals(userId)){
            throw new BusinessException(ErrorCode.NOT_YOUR_ORDER);
        }
//        2. 재고 확인
        if (!checkStock(order)) {
            orderService.failOrder(order.getId());
            throw new BusinessException(ErrorCode.FAILED_QUANTITY_PAYMENT);
        }
//        3. 결제 테이블 생성
        Payment payment = paymentRepository.findByOrderId(orderId);
        if(payment!=null){
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY);
        }
        payment = new Payment(orderId);
        paymentRepository.save(payment);

        log.info("[결제 진행 중] paymentStatus: {}", payment.getStatus());
        return  payment;
    }


    @Transactional
    public ApiResponse<?> createPayment(Long orderId,Long userId) {
        String lockKey = "lock:product:" + orderId; // 락 키 생성
        RLock lock = redissonClient.getLock(lockKey); // 분산락 객체 반환
        Order order = orderService.getOrderById(orderId);

        try {
            boolean isLocked = lock.tryLock(10, 2, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new IllegalArgumentException("락 획득 실패");
            }
//            1. 결제 테이블 정보, 재고확보
            Payment payment = initPayment(orderId,userId);

//            2-1. 고객사유로 결제 취소
            failStatus(order,payment);

//            2-2. 결제완료 & 주문성공
            completePayment(payment);
            orderService.orderComplete(orderId);

        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return ApiResponse.success("결제 성공");
    }

    /**
     * 결제화면까지 왔지만 결제하지 않음
     * */
    public void cancelStatus(Payment payment){
        LocalDateTime now = LocalDateTime.now();
        if (ChronoUnit.MINUTES.between(payment.getUpdatedAt(), now) >= 10 && payment.getStatus().equals(PaymentStatus.PROCESSING)) {
            // 10분 이상 지난 경우 결제 취소
            payment.statusCancel();
            paymentRepository.save(payment);
            orderService.failOrder(payment.getOrderId());
        }
        throw new BusinessException(ErrorCode.CANCELED_PAYMENT);
    }

    /**
     * 고객사유로 결제취소됨
     * */
    public void failStatus(Order order,Payment payment){
        if (Math.random() < 0.2) {
            increaseStock(order);
            failPayment(payment);
            orderService.failOrder(order.getId());
        }
        throw new BusinessException(ErrorCode.FAILED_PAYMENT);
    }


    /**
     * 제품 구매 성공
     * */
    public Boolean decreaseStock(Long productId,int quantity){
        String key = REDIS_STOCK_KEY + productId;
        Long stock = redisTemplate.opsForValue().decrement(key,quantity);

        if (stock==null || stock <0) {
            redisTemplate.opsForValue().increment(key,quantity);
            return false;
        }
        return true;
    }

    /**
     * 결제실패
     * */
    public void increaseStock(Order order){
        log.info("[결제 실패] 수량 복구 전 : "+ redisTemplate.opsForValue().get(REDIS_STOCK_KEY));
        List<OrderProducts> orderProductList = orderService.getOrderProductList(order);
        for (OrderProducts orderProducts : orderProductList) {
            String key = REDIS_STOCK_KEY +orderProducts.getProductId();
            redisTemplate.opsForValue().increment(key,orderProducts.getQuantity());
        }
        log.info("[결제 실패] 수량 복구 후 : "+ redisTemplate.opsForValue().get(REDIS_STOCK_KEY));
    }

//    제품 재고 조회
    private boolean checkStock(Order order){
        List<OrderProducts> orderProductList = orderService.getOrderProductList(order);
        for (OrderProducts orderProducts : orderProductList) {
            if(!decreaseStock(orderProducts.getProductId(),orderProducts.getQuantity())){
                return false;
            }
        }
        return true;
    }

    //    결제 완료
    private void completePayment(Payment payment) {
        payment.statusCompile();
        paymentRepository.save(payment);
    }
    //    결제 실패
    private void failPayment(Payment payment) {
        payment.statusFail();
        paymentRepository.save(payment);
    }


}
