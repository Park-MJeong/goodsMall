package com.hanghae.paymentservice.service;

import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.common.kafka.OrderRequestDto;
import com.hanghae.paymentservice.domain.PaymentRepository;
import com.hanghae.paymentservice.domain.entity.Payment;
import com.hanghae.paymentservice.domain.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
public class PaymentUnitTest {

    @InjectMocks
    PaymentService paymentService;
    @Mock
    PaymentRepository paymentRepository;
    @Mock
    RedissonClient redissonClient;
    @Mock
    RedisTemplate<String,Object> redisTemplate;

    private OrderRequestDto orderRequestDto1;
    private OrderRequestDto orderRequestDto2;
    private List<OrderRequestDto> orderRequestDtoList;

    private OrderEvent orderEvent;
    private Payment payment;

    @BeforeEach
    void setUp() {
        orderRequestDto1 = new OrderRequestDto(1L,1);
        orderRequestDto2 = new OrderRequestDto(2L,2);
        orderRequestDtoList = new ArrayList<>();

        orderRequestDtoList.add(orderRequestDto1);
        orderRequestDtoList.add(orderRequestDto2);

        orderEvent = OrderEvent.builder()
                .orderId(1L)
                .totalPrice(BigDecimal.valueOf(1000))
                .orderRequestDtoList(orderRequestDtoList)
                .build();
        payment = Payment.builder()
                .id(1L)
                .orderId(orderEvent.getOrderId())
                .status(PaymentStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("결제테이블 생성")
    void createPaymentTest(){
        when(paymentRepository.findByOrderId(orderEvent.getOrderId())).thenReturn(null);

        Payment payment = paymentService.createPayment(orderEvent.getOrderId());

        assertThat(payment.getOrderId()).isEqualTo(orderEvent.getOrderId());
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    @DisplayName("결제테이블 생성 유무")
    void isPaymentValidTest(){
        // Given
        Long orderId = 1L;
        Mockito.when(paymentRepository.findByOrderId(orderId)).thenReturn(payment);

        // When
        Payment result = paymentService.isPaymentValid(orderId);

        // Then
        assertNotNull(result);
        assertEquals(PaymentStatus.PENDING, result.getStatus());
        Mockito.verify(paymentRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    @DisplayName("결제테이블 생성 X")
    void isPaymentValidTestFail(){
        // Given
        Long orderId = 1L;
        Mockito.when(paymentRepository.findByOrderId(orderId)).thenReturn(null); //반환되는테이블 null 지정

        // Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> paymentService.isPaymentValid(orderId) // 테스트 대상 호출
        );
        assertEquals("결제정보가 존재하지 않습니다.",exception.getMessage());
        Mockito.verify(paymentRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    @DisplayName("결제 진행")
    void processPaymentTest(){



    }
}
