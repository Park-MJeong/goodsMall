package com.hanghae.orderservice.service;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.common.kafka.OrderRequestDto;
import com.hanghae.orderservice.client.ProductClient;
import com.hanghae.orderservice.client.dto.ProductIdAndQuantityDto;
import com.hanghae.orderservice.client.dto.ProductNameAndPriceDto;
import com.hanghae.orderservice.client.dto.ProductResponseDto;
import com.hanghae.orderservice.domain.OrderProductRepository;
import com.hanghae.orderservice.domain.OrderRepository;
import com.hanghae.orderservice.domain.entity.Order;
import com.hanghae.orderservice.domain.entity.OrderProducts;
import com.hanghae.orderservice.domain.entity.OrderStatus;
import com.hanghae.orderservice.dto.*;
import com.hanghae.orderservice.kafka.producer.OrderPaymentProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.hanghae.common.util.RedisKeyUtil.getStockKey;



@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductClient productClient;
    private final RedisTemplate<String,Integer> redisTemplate;
    private final OrderPaymentProducer orderPaymentProducer;
    private final JdbcTemplate jdbcTemplate;


    /**
     * 주문리스트 조회
     */
    @Transactional(readOnly = true)
    public ApiResponse<?> getOrderList(Long userId, int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
//        1. 해당 아이디의 주문 내역 리스트 조회
        Page<Order> orderList = orderRepository.getOrderList(userId,pageable);
        if(orderList.getTotalElements()==0){
            return ApiResponse.success("아직 주문내역이 존재하지 않습니다.");
        }
//        2. 각 주문의 내역 추출
        List<Order> orders = orderList.getContent();
        List<OrderListDto> listDto = orders.stream().map(
                order-> {
//                    3. 해당 주문의 물품리스트 정보
                    List<OrderProductDto> orderProductDtoList = orderProductDtoList(order);
                return new OrderListDto(order,orderProductDtoList);}).toList();

        return ApiResponse.success(listDto);
    }

    /**
     * 주문 내역 상세 (해당 주문목록 제품 리스트)
     */
    @Transactional(readOnly = true)
    public ApiResponse<?> getOrderProduct(Long orderId){
        Order order = orderRepository.getOrderProductsList(orderId);

        List<OrderProductDto> orderProductDtoList = orderProductDtoList(order);
        OrderListDto listDto = new OrderListDto(order,orderProductDtoList);

        return ApiResponse.success(listDto);
    }

    /**
     *  상품 구매
     */
    @Transactional
    public ApiResponse<?> createOrder(Long userId,OrderListRequestDto dto){
        BigDecimal totalPrice =BigDecimal.ZERO;
//        1. 상품 재고 파악
        if(!checkStock(dto)) throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);

//        2. 주문 테이블 생성
        Order order = createOrderInfo(userId,totalPrice);
//        3. 주문 상품 테이블 생성
        List<OrderProductDto> orderProductDtoList = createOrderProduct(order,dto);

//        4. 반환값 변환
        OrderListDto listDto = new OrderListDto(order,orderProductDtoList);

//        5. 카프카 발행
        KafkaRequestDto kafkaRequestDto = new KafkaRequestDto(order.getId(),order.getTotalPrice(),dto.getOrderRequestDtoList());
        orderPaymentProducer.createOrder(kafkaRequestDto);

        return ApiResponse.success(listDto);
    }

//    상품 재고 파악
    private boolean checkStock(OrderListRequestDto dto){
        for(OrderRequestDto productList : dto.getOrderRequestDtoList()){
            long productId = productList.getProductId();
            int quantity = productList.getQuantity();
            Integer stock =redisTemplate.opsForValue().get(getStockKey(productId));
            log.info("재고 체크 Id: {} ",productId);
            if(stock == null || stock<quantity){
                log.info("재고 부족 Id{} ",productId);
                return false;
            }
        }
        return true;
    }

//    주문 테이블 생성
    private Order createOrderInfo(Long userId,BigDecimal totalPrice){
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PROCESSING)
                .totalPrice(totalPrice)
                .build();
        orderRepository.save(order);
        return order;
    }

//    주문 상품테이블 생성 및 전체가격 저장
    private List<OrderProductDto> createOrderProduct(Order order,OrderListRequestDto dto){
        BigDecimal totalPrice =BigDecimal.ZERO;
        List<OrderProductDto> orderProductDtoList = new ArrayList<>();

        for(OrderRequestDto productList : dto.getOrderRequestDtoList()){
            long productId = productList.getProductId();
            ProductNameAndPriceDto responseDto = availableProducts(productId);

            OrderProducts orderProducts = OrderProducts.builder()
                    .productId(productId)
                    .price(responseDto.getProductPrice())
                    .quantity(productList.getQuantity())
                    .order(order)
                    .build();

            totalPrice = totalPrice.add(orderProducts.getPrice().multiply(BigDecimal.valueOf(orderProducts.getQuantity())));
            orderProductRepository.save(orderProducts);

            OrderProductDto orderProductDto = new OrderProductDto(orderProducts,responseDto.getProductName());
            orderProductDtoList.add(orderProductDto);
        }
        order.updatePrice(totalPrice);
        orderRepository.save(order);

        return orderProductDtoList;
    }



////    주문 테이블 상태 변경
    @Transactional
    public void changeOrderStatus(Long orderId,OrderStatus orderStatus){
        Order order = getOrderById(orderId);
        Order newOrder = order.toBuilder()
                .status(orderStatus)
                .build();
        orderRepository.orderStatusUpdating(orderStatus,orderId);
        orderRepository.save(newOrder);
    }
//    주문 테이블 상태 변경
    @Transactional
    public void changeOrderListStatus(List<Long> orderIds, OrderStatus orderStatus){
            String sql = "UPDATE `orders` SET `status` = ? WHERE `id` = ?";
//            for(Long orderId : orderIds){
//                System.out.println(orderId);
//            }
        // 배치 처리용 파라미터 생성
        List<Object[]> batchArgs = orderIds.stream()
                .map(orderId -> new Object[]{orderStatus.name(), orderId})
                .toList();

        // 배치 업데이트 실행
        int[] updateCounts = jdbcTemplate.batchUpdate(sql, batchArgs);

        // 로그 처리
        log.info("주문 상태 변경 완료: 총 {}건 처리됨", updateCounts.length);

    }

//    주문 상품 장바구니에서 삭제 구현해야함



    /**
     * 주문 취소 또는 반품시 상태변경
     */
    public ApiResponse<?> cancelOrder(Long orderId){
        Order order =orderRepository.findByOrderId(orderId).orElseThrow(()->new BusinessException(ErrorCode.ORDER_NOT_FOUND));
//        1. 취소 가능한 주문인지 확인
        if(order.getStatus()!= OrderStatus.COMPLETE  && order.getStatus()!=OrderStatus.RETURN_COMPLETE){
            throw new BusinessException(ErrorCode.ORDER_CANCELLED_FAILED);
        }
//        2.제품 테이블 재고 반영
        for(OrderProducts orderProducts : order.getOrderProducts()){
            ProductIdAndQuantityDto productIdAndQuantityDto = ProductIdAndQuantityDto.builder()
                    .productId(orderProducts.getProductId())
                    .quantity(orderProducts.getQuantity())
                    .build();
           productClient.increaseStock(productIdAndQuantityDto);
        }
//        3.주문 테이블 상태, 취소로 변경
        changeOrderStatus(orderId,OrderStatus.CANCELED);

        log.info("상태변경{},날짜변경{}",order.getStatus(),order.getUpdatedAt());

        return ApiResponse.success(order.getStatus()+"처리 되었습니다.");
    }



//     주문테이블 정보 가져오기
    public Order getOrderById(Long orderId){
        return orderRepository.findByOrderId(orderId).orElseThrow(()->new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

//    public List<OrderProducts> getOrderProductList(Order order){
//        return orderProductRepository.findByOrder(order);
//    }

    //    주문상품테이블에서 상품이름이 추가된 dto
    private List<OrderProductDto> orderProductDtoList(Order order){
        return order.getOrderProducts().stream().map(
                orderProducts -> {
                    ProductNameAndPriceDto productNameAndPriceDTO = availableProducts(orderProducts.getProductId());
                    return new OrderProductDto(orderProducts,productNameAndPriceDTO.getProductName());
                }).toList();
    }

    //    상품정보
    private ProductResponseDto productInfo(Long productId){
        return productClient.information(productId);
    }
//    구매가능한 상품정보만
    private ProductNameAndPriceDto availableProducts(Long productId){
        return  productClient.availableProducts(productId);
    }

    /**
     * 해당 주문의 상품id,구매원하는 정보
     * */
    public List<OrderProductStock> getOrderProductStockList(Long orderId){
        return orderProductRepository.findStockByOrderId(orderId);
    }

}
