package com.hanghae.orderservice.service;

import com.goodsmall.modules.order.event.OrderStatus;
import com.goodsmall.modules.order.domain.OrderRepository;
import com.goodsmall.modules.order.domain.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public abstract class StatusService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public StatusService(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @Scheduled(cron = "0 0 0 * * *") //자정
    public void updateStatus() {
        LocalDateTime now = LocalDateTime.now();
//        주문완료 -> 배송중
        List<Order> completedOrders = orderRepository.findByStatus(OrderStatus.COMPLETE,now.minusDays(1));
        for (Order order : completedOrders) {
            order.setStatus(OrderStatus.DELIVERY_NOW);
            order.setUpdatedAt(now);
            orderRepository.save(order);
            log.info("상태변경{},날짜변경{}",order.getStatus(),order.getUpdatedAt());

        }
//        배송중 -> 배송완료
        List<Order> deliveringOrders = orderRepository.findByStatus(OrderStatus.DELIVERY_NOW,now.minusDays(1));
        for (Order order : deliveringOrders) {
            order.setStatus(OrderStatus.DELIVERY_COMPLETE);
            order.setUpdatedAt(now);
            orderRepository.save(order);
            log.info("상태변경{},날짜변경{}",order.getStatus(),order.getUpdatedAt());

        }
//        배송완료->반품불가
        List<Order> deliveredOrders = orderRepository.findByStatus(OrderStatus.DELIVERY_COMPLETE,now.minusDays(2));
        for (Order order : deliveredOrders) {
            order.setStatus(OrderStatus.RETURN_NOT_ALLOWED);
            order.setUpdatedAt(now);
            orderRepository.save(order);
            log.info("상태변경{},날짜변경{}",order.getStatus(),order.getUpdatedAt());
        }
//        반품신청->반품완료 + 재고반영
        List<Order> returnOrders = orderRepository.findByStatus(OrderStatus.RETURN_NOW,now.minusDays(1));
        for (Order order : returnOrders) {
            order.setStatus(OrderStatus.RETURN_COMPLETE);
            orderService.cancelOrder(order.getId());
        }
    }



}
