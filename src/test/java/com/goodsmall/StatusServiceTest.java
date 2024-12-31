package com.goodsmall;

import com.goodsmall.modules.order.event.OrderStatus;
import com.goodsmall.modules.order.domain.OrderRepository;
import com.goodsmall.modules.order.domain.entity.Order;
import com.goodsmall.modules.order.service.StatusService;
import com.goodsmall.modules.product.domain.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import static org.mockito.Mockito.*;

class StatusServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;

    private StatusService statusService;
    private Clock clock;

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        clock = Clock.fixed(Instant.parse("2023-01-01T00:00:00Z"), ZoneId.systemDefault());
//        statusService = new StatusService(orderRepository, productRepository) {
//            @Override
//            protected LocalDateTime now() {
//                return LocalDateTime.now(clock);
//            }
//        };
//    }

    @Test
    void testUpdateStatus() {
        // 테스트 데이터 설정
        Order completedOrder = new Order();
        completedOrder.setStatus(OrderStatus.COMPLETE);
        completedOrder.setCreatedAt(LocalDateTime.now(clock).minusDays(2));

        Order deliveringOrder = new Order();
        deliveringOrder.setStatus(OrderStatus.DELIVERY_NOW);
        deliveringOrder.setUpdatedAt(LocalDateTime.now(clock).minusDays(2));

        // Mock 설정
        when(orderRepository.findByStatus(eq(OrderStatus.COMPLETE), any())).thenReturn(Arrays.asList(completedOrder));
        when(orderRepository.findByStatus(eq(OrderStatus.DELIVERY_NOW), any())).thenReturn(Arrays.asList(deliveringOrder));

        // 메서드 실행
        statusService.updateStatus();

        // 검증
        verify(orderRepository).save(argThat(order -> order.getStatus() == OrderStatus.DELIVERY_NOW));
        verify(orderRepository).save(argThat(order -> order.getStatus() == OrderStatus.DELIVERY_COMPLETE));
    }
}
