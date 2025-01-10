package com.hanghae.paymentservice.service;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.paymentservice.client.OrderClient;
import com.hanghae.paymentservice.client.dto.OrderProductStockList;
import com.hanghae.paymentservice.domain.PaymentRepository;
import com.hanghae.paymentservice.domain.entity.Payment;
import com.hanghae.paymentservice.domain.entity.PaymentStatus;
import com.hanghae.paymentservice.kafka.producer.PaymentOrderProducer;
import com.hanghae.paymentservice.service.scripts.RedisStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;

import static com.hanghae.common.util.RedisKeyUtil.getStockKey;

@Service
@Slf4j(topic = "결제화면")
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final RedisTemplate<String,Integer> redisTemplate;
    private final RedissonClient redissonClient;
    private final OrderClient orderClient;
    private final PaymentOrderProducer paymentOrderProducer;
    private final RedisStockService redisStockService;


    /**
     * 재고 파악 후 결제 테이블 생성
     * */
    public void initPayment(OrderEvent orderEvent) {
        Long orderId = orderEvent.getOrderId();
        String lockKey = getStockKey(orderId);
        RLock lock = redissonClient.getLock(lockKey); // 분산락 객체 반환

        try {
            if (!lock.tryLock(10, 2, TimeUnit.SECONDS)) {
                throw new IllegalArgumentException("락 획득 실패");
            }
//          1. 재고 확인
            if (!checkStock(orderEvent.getProductId(),orderEvent.getQuantity())) {
                throw new BusinessException(ErrorCode.FAILED_QUANTITY_PAYMENT);
            }
//          2. 결제 테이블 생성
            createPayment(orderId);

        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);

        }
        finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

//    결제 테이블 생성
    public void createPayment(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment != null) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY);
        }
        payment = new Payment(orderId);
        paymentRepository.save(payment);
        log.info("[결제 테이블 생성 완료] paymentStatus: {}", payment.getStatus());
    }

//    api에서 결제화면 진입전 테이블 생성유무 확인
    public boolean isPaymentValid(Long orderId) {
        Payment payment = getValidPayment(orderId);
        if(!payment.getStatus().equals(PaymentStatus.PENDING)){
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);
        }
        return true;
    }

    /**
     * 결제 완료
     * */
    @Transactional
    public ApiResponse<?> processPayment(Long orderId) {
        Payment payment = getValidPayment(orderId);

        changePaymentStatus(payment.getId(),PaymentStatus.COMPLETE);
        return ApiResponse.success(payment);
    }



    /**
     * 단일 제품, 재고 감소
     * */
    public boolean checkStock(Long productId,int quantity){
        String key = getStockKey(productId);
        Long stock = redisTemplate.opsForValue().decrement(key,quantity);

        if (stock==null || stock <0) {
//            재고 부족시, 원상복귀
            redisTemplate.opsForValue().increment(key,quantity);
            return false;
        }
        return true;
    }

//    결제 테이블 상태 변경
    private void changePaymentStatus(Long paymentId,PaymentStatus status){
        Payment payment = getValidPayment(paymentId);
        Payment updatePayment = payment.toBuilder()
                .status(status)
                .build();
        paymentRepository.save(updatePayment);

    }

    // 해당 주문테이블로 생성된 결제 테이블 유무
    private Payment getValidPayment(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        return payment;
    }

//    결제테이블 정보 조회
    private Payment getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        return payment;
    }



//    해당 주문의 제품id, 원하는 수량
    private OrderProductStockList getOrderProductStockList(Long orderId){
       return orderClient.orderProductStock(orderId);
    }

    //    실패시 order-service 로 보내는 kafka
    public void sendFailurePayment(OrderEvent orderEvent) {
        paymentOrderProducer.failurePayment(orderEvent);
    }

}
