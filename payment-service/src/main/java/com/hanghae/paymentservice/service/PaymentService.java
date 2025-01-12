package com.hanghae.paymentservice.service;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.common.kafka.OrderRequestDto;
import com.hanghae.common.kafka.PaymentEvent;
import com.hanghae.paymentservice.client.OrderClient;
import com.hanghae.paymentservice.domain.PaymentRepository;
import com.hanghae.paymentservice.domain.entity.Payment;
import com.hanghae.paymentservice.domain.entity.PaymentStatus;
import com.hanghae.paymentservice.dto.PaymentStatusDto;
import com.hanghae.paymentservice.kafka.producer.PaymentOrderProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.hanghae.common.util.RedisKeyUtil.getPaymentKey;
import static com.hanghae.common.util.RedisKeyUtil.getStockKey;

@Service
@Slf4j(topic = "결제화면")
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final RedisTemplate<String,Object> redisTemplate;
    private final RedissonClient redissonClient;
    private final PaymentOrderProducer paymentOrderProducer;


    /**
     * 재고 파악 후 결제 테이블 생성
     * */
    public void initPayment(OrderEvent orderEvent) {
        List<OrderRequestDto> orderRequestDtos = orderEvent.getOrderRequestDtoList();

        Long orderId = orderEvent.getOrderId();
        String lockKey = getStockKey(orderId);
        RLock lock = redissonClient.getLock(lockKey); // 분산락 객체 반환

        try {
            if (!lock.tryLock(10, 2, TimeUnit.SECONDS)) {
                throw new IllegalArgumentException("락 획득 실패");
            }
//          1. 재고 확인
            if (!checkStock(orderRequestDtos)){
//                1-1. 재고 확보 실패, 주문테이블 상태 변경 이벤트 발행
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
    public Payment isPaymentValid(Long orderId) {

        Payment payment = getValidPayment(orderId);
        if(!payment.getStatus().equals(PaymentStatus.PENDING)){
            log.info("[테이블 생성유무 확인] -------- 결제테이블 생성되지 않음 --------");
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);
        }
        log.info("[테이블 생성유무 확인] -------- 결제테이블 생성되어있음 --------");
        return payment;
    }

    /**
     * 결제 진행
     * */
    @Transactional
    public ApiResponse<?> processPayment(Payment payment) {
        String key = getPaymentKey(payment.getId());
//        10분이상 결제 되지않으면 결제 취소
        redisTemplate.opsForValue().set(key,payment.getStatus(),Duration.ofMinutes(10));

        changePaymentStatus(payment.getId(),PaymentStatus.COMPLETE);
        successPayment(payment.getOrderId());
        return ApiResponse.success("결제완료되었습니다. 결제 상태: "+payment.getStatus());
    }

    /**
     * 결제 10분이내 진행되지않으면 취소
     * 레디스 ttl 이용
     * */
    public void cancelPayment(Long paymentId){
        PaymentStatusDto paymentStatusDto= getStatus(paymentId);
        PaymentStatus status = paymentStatusDto.getPaymentStatus();
        Long orderId = paymentStatusDto.getOrderId();
        if(status.equals(PaymentStatus.PENDING)){
            changePaymentStatus(paymentId,PaymentStatus.CANCELED);
            log.info("10분이내 결제가 이루어지지않아 결제 취소되었습니다.");
            failurePayment(orderId);

        }
    }

//    재고 파악
    private boolean checkStock(List<OrderRequestDto> orderRequestDtos){
        Map<String,Integer> decreaseStock = new ConcurrentHashMap<>();

        for (OrderRequestDto orderRequestDto : orderRequestDtos) {
            Long productId = orderRequestDto.getProductId();
            Integer quantity = orderRequestDto.getQuantity();
            String key = getStockKey(productId);
//            1. 재고 감소, 재고 확보하기
            Long stock = redisTemplate.opsForValue().decrement(key,quantity);
            decreaseStock.put(key,quantity);
            if (stock==null || stock <0) {
//            2. 재고 부족, 이전 값 되돌리기
                stockRollback(decreaseStock);
                return false;
            }
        }
        return true;
    }

//    재고확보 실패,rollback
    private void stockRollback(Map<String,Integer> decreaseStock){
        decreaseStock.forEach((key, quantity) ->
                redisTemplate.opsForValue().increment(key, quantity)
        );
    }

//    결제 테이블 상태 변경
    private void changePaymentStatus(Long paymentId,PaymentStatus status){
        Payment payment = getPaymentInfo(paymentId);
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

//    결제 테이블 정보
    private Payment getPaymentInfo(Long paymentId){
        return paymentRepository.findById(paymentId).orElseThrow(
                ()->new BusinessException(ErrorCode.PAYMENT_NOT_FOUND)
        );
    }


    private PaymentStatusDto getStatus(Long paymentId){
        return paymentRepository.findStatusById(paymentId);
    }

    public void successPayment(Long orderId){
        PaymentEvent paymentEvent = PaymentEvent.builder()
                .orderId(orderId)
                .build();
        paymentOrderProducer.failurePayment(paymentEvent);
    }
    //    재고확보 실패시 order-service 로 보내는 kafka
    public void stockNotAvailable(OrderEvent orderEvent) {
        paymentOrderProducer.stockNotAvailable(orderEvent);
    }

    //    결제 취소,실패 시 order-service 로 보내는 kafka
    public void failurePayment(Long orderId) {
        PaymentEvent paymentEvent = PaymentEvent.builder()
                .orderId(orderId)
                .build();
        paymentOrderProducer.failurePayment(paymentEvent);
    }
}
