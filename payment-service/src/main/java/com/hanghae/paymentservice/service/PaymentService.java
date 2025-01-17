package com.hanghae.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.common.kafka.OrderRequestDto;
import com.hanghae.paymentservice.domain.PaymentRepository;
import com.hanghae.paymentservice.domain.entity.Payment;
import com.hanghae.paymentservice.domain.entity.PaymentStatus;
import com.hanghae.paymentservice.dto.PaymentStatusDto;
import com.hanghae.paymentservice.kafka.producer.PaymentOrderProducer;
import com.hanghae.paymentservice.kafka.producer.PaymentProductProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    private final PaymentProductProducer paymentProductProducer;
    private final JdbcTemplate jdbcTemplate;
    private final Map<String,Integer> decreaseStock = new ConcurrentHashMap<>();



    /**
     * 재고 파악 후 결제 테이블 생성
     * */
    @Transactional
    public void initPayment(List<OrderEvent> orderEvents) {
        List<Payment> paymentsToSave = new ArrayList<>(); // saveAll에 사용할 리스트
        for (OrderEvent orderEvent : orderEvents) {
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
//          2. 결제 테이블 생성
                Payment newPayment = createPayment(orderId);
                paymentsToSave.add(newPayment);
                log.info("[결제 테이블 생성 완료] paymentStatus: {}", newPayment.getStatus());

                //        10분이상 결제 되지않으면 결제 취소
                String key = getPaymentKey(newPayment.getId());
                redisTemplate.opsForValue().set(key,orderEvent,Duration.ofMinutes(10));

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
        if (!paymentsToSave.isEmpty()) {
            batchInsertPayments(paymentsToSave);
            log.info("[결제 테이블 생성 완료] 저장된 결제 건수: {}", paymentsToSave.size());
        }

    }

//    결제 테이블 생성
    public Payment createPayment(Long orderId) {
        return  Payment.builder()
                .id(orderId)
                .orderId(orderId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(PaymentStatus.PENDING)
                .build();
//        paymentRepository.save(payment);
    }
    private void batchInsertPayments(List<Payment> paymentsToSave) {
        String sql = "INSERT INTO payments (id, order_id, created_at, updated_at, status) VALUES (?, ?, ?, ?, ?)";
        List<Object[]> batchArgs = paymentsToSave.stream()
                .map(payment -> new Object[]{
                        payment.getId(), // id
                        payment.getOrderId(), // orderId
                        payment.getCreatedAt(), // createdAt
                        payment.getUpdatedAt(), // updatedAt
                        payment.getStatus().name() // status
                })
                .toList();

        try {
            jdbcTemplate.batchUpdate(sql, batchArgs);
        } catch (Exception e) {
            e.printStackTrace(); // 예외 출력
            System.out.println("Error during batch insert: " + e.getMessage());
        }
    }

//    api에서 결제화면 진입전 테이블 생성유무 확인
    public Payment isPaymentValid(Long orderId) {
        Payment payment = getValidPayment(orderId);
        if(payment.getStatus() != PaymentStatus.PENDING){
            log.info("[테이블 생성유무 확인] -------- 이미 결제완료/결제취소  --------");
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
        if(Math.random() < 0.2){
            changePaymentStatus(payment.getId(), PaymentStatus.FAILED);
            log.info("결제 실패: 결제 아이디 " + payment.getId());
            failurePayment(payment.getOrderId());
            stockRollback(decreaseStock);
            return ApiResponse.success("결제실패하였습니다. 결제 상태: "+payment.getStatus());
        }

        changePaymentStatus(payment.getId(),PaymentStatus.COMPLETE);
        OrderEvent orderEvent = convertToOrderEvent( redisTemplate.opsForValue().get(key));
        successPayment(orderEvent);
        return ApiResponse.success("결제완료되었습니다. 결제 상태: "+payment.getStatus());
    }

    public OrderEvent convertToOrderEvent(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            String jsonString = objectMapper.writeValueAsString(object);
            return objectMapper.readValue(jsonString,OrderEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 결제 10분이내 진행되지않으면 취소
     * 레디스 ttl 이용
     * */
    public void cancelPayment(Long paymentId){
        PaymentStatusDto paymentStatusDto= getStatus(paymentId);
        PaymentStatus status = paymentStatusDto.getPaymentStatus();
        Long orderId = paymentStatusDto.getOrderId();

        if(status ==PaymentStatus.PENDING){
            changePaymentStatus(paymentId,PaymentStatus.CANCELED);
            log.info("10분이내 결제가 이루어지지않아 결제 취소되었습니다.");
            failurePayment(orderId);

        }
    }

//    재고 파악
    private boolean checkStock(List<OrderRequestDto> orderRequestDtos){

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

//    private boolean checkStock(List<OrderRequestDto> orderRequestDtos) {
//        for (OrderRequestDto orderRequestDto : orderRequestDtos) {
//            Long productId = orderRequestDto.getProductId();
//            int quantity = orderRequestDto.getQuantity();
//            String key = getStockKey(productId);
//            // Lua 스크립트 실행
//            RFuture<Long> resultFuture = redissonClient.getScript().evalAsync(
//                    RScript.Mode.READ_WRITE,
//                    """
//                                  local key = KEYS[1]
//                                   local quantity = tonumber(ARGV[1])
//                                   local currentStock = redis.call('GET', key)
//                                   if not currentStock or tonumber(currentStock) < quantity then
//                                       return -1
//                                   end
//                                   return redis.call('DECRBY', stockKey, quantity)
//                            """
//                    ,
//                    RScript.ReturnType.INTEGER,
//                    Collections.singletonList(key),
//                    quantity);
//            try {
//                Long result = resultFuture.toCompletableFuture().get(1, TimeUnit.SECONDS);
//                return result != -1;
//            } catch (Exception e) {
//                log.error("Redis Lua 스크립트 실행 중 오류 발생", e);
//                return false;
//            }
//        }
//
//        return true;
//    }
//    재고확보 실패,rollback
    private void stockRollback(Map<String,Integer> decreaseStock){
        decreaseStock.forEach((key, quantity) ->
                redisTemplate.opsForValue().increment(key, quantity)
        );
        log.info("롤백");
    }

//    결제 테이블 상태 변경
    private void changePaymentStatus(Long paymentId, PaymentStatus status){
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
            log.info("[테이블 생성유무 확인] -------- 결제테이블 생성되지 않음 --------");
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

//    결제 성공시 order-service,product-service 로 보내는 kafka
    @Transactional
    public void successPayment(OrderEvent orderEvent){
        paymentProductProducer.successPayment(orderEvent);
        paymentOrderProducer.successPayment(orderEvent);
    }

    //    재고확보 실패시 order-service 로 보내는 kafka
    public void stockNotAvailable(OrderEvent orderEvent) {
        paymentOrderProducer.stockNotAvailable(orderEvent);
    }
    //    결제 취소,실패 시 order-service 로 보내는 kafka
    public void failurePayment(Long orderId) {
        paymentOrderProducer.failurePayment(orderId);
    }
}
