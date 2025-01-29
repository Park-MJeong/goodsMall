package com.hanghae.paymentservice.stockTest;

import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.common.kafka.OrderRequestDto;
import com.hanghae.paymentservice.domain.PaymentRepository;
import com.hanghae.paymentservice.domain.entity.Payment;
import com.hanghae.paymentservice.kafka.producer.PaymentOrderProducer;
import com.hanghae.paymentservice.kafka.producer.PaymentProductProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.hanghae.common.util.RedisKeyUtil.getStockKey;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedissonService {
    private final PaymentRepository paymentRepository;
    private final RedisTemplate<String,Object> redisTemplate;
    private final RedissonClient redissonClient;
    private final PaymentOrderProducer paymentOrderProducer;
    private final PaymentProductProducer paymentProductProducer;
    private final JdbcTemplate jdbcTemplate;
    private final Map<String,Integer> decreaseStock = new ConcurrentHashMap<>();


    /**
     * 재고 파악 후 결제 테이블 생성
     * */
    @Transactional
    public void initPayment(OrderEvent orderEvent) {

        Long orderId = orderEvent.getOrderId();
        String lockKey = "lock:product:"+orderId;
        RLock lock = redissonClient.getLock(lockKey); // 분산락 객체 반환

        try {
            if (!lock.tryLock(10, 2, TimeUnit.SECONDS)) {
                throw new IllegalArgumentException("락 획득 실패");
            }
            Payment payment = paymentRepository.findByOrderId(orderEvent.getOrderId());
            if (payment != null) {
                throw new BusinessException(ErrorCode.PAYMENT_ALREADY);
            }
//          1. 재고 확인
            List<OrderRequestDto> orderRequestDtos = orderEvent.getOrderRequestDtoList();
            if (!checkStock(orderRequestDtos)){
//                1-1. 재고 확보 실패, 주문테이블 상태 변경 이벤트 발행
                throw new BusinessException(ErrorCode.FAILED_QUANTITY_PAYMENT);
            }

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
    //    재고 파악
    public boolean checkStock(List<OrderRequestDto> orderRequestDtos){

        for (OrderRequestDto orderRequestDto : orderRequestDtos) {
            Long productId = orderRequestDto.getProductId();
            Integer quantity = orderRequestDto.getQuantity();
            String key = getStockKey(productId);
            log.info(key);
//            1. 재고 감소, 재고 확보하기
            Long stock = redisTemplate.opsForValue().decrement(key,quantity);
            log.info("재고: {}, 제품 아이디: {}",stock,productId);
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
    public void stockRollback(Map<String,Integer> decreaseStock){
        decreaseStock.forEach((key, quantity) ->
                redisTemplate.opsForValue().increment(key, quantity)
        );
        log.info("롤백");
    }

}
