package com.hanghae.orderservice.service;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.orderservice.domain.PaymentRepository;
import com.hanghae.orderservice.domain.entity.Order;
import com.hanghae.orderservice.domain.entity.OrderProducts;
import com.hanghae.orderservice.domain.entity.Payment;
import com.hanghae.orderservice.event.PaymentStatus;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class PaymentService {
    private final  OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final RedisTemplate<String,Integer> redisTemplate;
    private final RedissonClient redissonClient;
    private static final String REDIS_STOCK_KEY = "product:stock:";


    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<?> createPayment(long orderId) {
        String lockKey = "lock:product:" + orderId; // 락 키 생성
        RLock lock = redissonClient.getLock(lockKey); // 분산락 객체 반환
        Order order = orderService.getOrderById(orderId);
        Payment payment = new Payment(orderId);

        try {
            boolean isLocked = lock.tryLock(10, 2, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new IllegalArgumentException("락 획득 실패");
            }

//            결제테이블 생셩
            paymentRepository.save(payment);
            // 재고 체크 및 결제 상태 처리
            if (!checkStock(order)) {
                failPayment(payment);
                orderService.failOrder(order.getId());
                return ApiResponse.createException(ErrorCode.FAILED_QUANTITY_PAYMENT);
            }

            LocalDateTime now = LocalDateTime.now();
            if(now.isBefore(payment.getCreatedAt())){
                failPayment(payment);
                orderService.failOrder(order.getId());
                return ApiResponse.createException(ErrorCode.FAILED_TIME_PAYMENT);
            }

//            결제완료 & 주문성공
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
    public ApiResponse<?> cancelStatus(long orderId){
        Payment payment = new Payment(orderId);

        LocalDateTime now = LocalDateTime.now();
        if(Math.random()<0.2){
            if (ChronoUnit.MINUTES.between(payment.getUpdatedAt(), now) >= 10 || payment.getStatus().equals(PaymentStatus.PROCESSING)) {
                // 10분 이상 지난 경우 결제 취소
                orderService.failOrder(orderId);
                failPayment(payment);
                return ApiResponse.success("10분 이내 결제 완료되지 않아 주문이 취소되었습니다.");
            }
        }

        return null;
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
     * 제품 재고 조회
     * */
    private boolean checkStock(Order order){
        List<OrderProducts> orderProductList = orderService.getOrderProductList(order);
        for (OrderProducts orderProducts : orderProductList) {
            if(!decreaseStock(orderProducts.getProductId(),orderProducts.getQuantity())){
                return false;
            }
        }
        return true;
    }

}
