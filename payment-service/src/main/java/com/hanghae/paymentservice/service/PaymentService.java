package com.hanghae.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.common.kafka.OrderRequestDto;
import com.hanghae.paymentservice.client.OrderClient;
import com.hanghae.paymentservice.client.dto.OrderProductStock;
import com.hanghae.paymentservice.domain.PaymentRepository;
import com.hanghae.paymentservice.domain.entity.Payment;
import com.hanghae.paymentservice.domain.entity.PaymentStatus;
import com.hanghae.paymentservice.dto.PaymentStatusDto;
import com.hanghae.paymentservice.kafka.producer.PaymentOrderProducer;
import com.hanghae.paymentservice.kafka.producer.PaymentProductProducer;
import com.hanghae.paymentservice.stockTest.LuaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.hanghae.common.util.RedisKeyUtil.getPaymentKey;
import static com.hanghae.common.util.RedisKeyUtil.getStockKey;

@Service
@Slf4j(topic = "결제화면")
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final RedisTemplate<String,Object> redisTemplate;
    private final LuaService luaService;
    private final RedissonClient redissonClient;
    private final PaymentOrderProducer paymentOrderProducer;
    private final PaymentProductProducer paymentProductProducer;
    private final JdbcTemplate jdbcTemplate;
    private final OrderClient orderClient;
    private final Map<String,Integer> decreaseStock = new ConcurrentHashMap<>();



    /**
     * 재고 파악 후 결제 테이블 생성
     * */
    @Transactional
    public void initPayment(OrderEvent orderEvent) {
        Long orderId = orderEvent.getOrderId();
        Payment payment = paymentRepository.findByOrderId(orderEvent.getOrderId());
        if (payment != null) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY);
        }
        try {
            for (OrderRequestDto dto : orderEvent.getOrderRequestDtoList()) {
                String stockKey = getStockKey(dto.getProductId());
//                1. 재고 확인
                boolean success = luaService.decreaseStock(stockKey, dto.getQuantity());
                if(success){
                    decreaseStock.put(stockKey,dto.getQuantity()); // 상품리스트중 한개라도 재고확보 실패시 롤백위해 필요
                } else {
                    throw new BusinessException(ErrorCode.FAILED_QUANTITY_PAYMENT);
                }

//              2. 결제 테이블 생성
                Payment newPayment = createPayment(orderId);
                paymentRepository.save(newPayment);
                log.info("[결제 테이블 생성 완료] paymentStatus: {}", newPayment.getStatus());

                //        10분이상 결제 되지않으면 결제 취소
                String key = getPaymentKey(newPayment.getId());
                redisTemplate.opsForValue().set(key,orderEvent,Duration.ofMinutes(10));

            }
        } catch (Exception e) {
//            재고 확보 실패시 주문실패
            stockNotAvailable(orderEvent);
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
        if(status ==PaymentStatus.PENDING){
//            orderId로 해당 상품과 갯수리스트 가져옴
            Long orderId = paymentStatusDto.getOrderId();
//            확보했던 재고 rollback
            List<OrderProductStock> orderProductStockList = getStockList(orderId);
            for(OrderProductStock orderProductStock:orderProductStockList){
                String key = getStockKey(orderProductStock.getOrderProductId());
                decreaseStock.put(key,orderProductStock.getQuantity());
            }

            changePaymentStatus(paymentId,PaymentStatus.CANCELED);
            log.info("10분이내 결제가 이루어지지않아 결제 취소되었습니다.");
            stockRollback(decreaseStock);
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


//    재고확보 실패 or 결제 취소시 재고 rollback
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

    private List<OrderProductStock> getStockList(Long orderId){
        return orderClient.orderProductStock(orderId);
    }
}
