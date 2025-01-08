package com.hanghae.orderservice.service;


import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.orderservice.client.CartClient;
import com.hanghae.orderservice.client.ProductClient;
import com.hanghae.orderservice.client.dto.CartProductDto;
import com.hanghae.orderservice.client.dto.ProductResponseDto;
import com.hanghae.orderservice.domain.OrderProductRepository;
import com.hanghae.orderservice.domain.OrderRepository;
import com.hanghae.orderservice.domain.entity.Order;
import com.hanghae.orderservice.domain.entity.OrderProducts;
import com.hanghae.orderservice.dto.Order.OrderListDto;
import com.hanghae.orderservice.dto.Order.OrderListRequestDto;
import com.hanghae.orderservice.dto.Order.OrderProductDto;
import com.hanghae.orderservice.dto.Order.OrderRequestDto;
import com.hanghae.orderservice.util.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductClient productClient;
    private final CartClient cartClient;
    private final RedisTemplate<String,Integer> redisTemplate;
    private static final String REDIS_STOCK_KEY = "product:stock:";

    public OrderService(OrderRepository orderRepository, OrderProductRepository orderProductRepository, ProductClient productClient, CartClient cartClient, RedisTemplate<String, Integer> redisTemplate) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.productClient = productClient;
        this.cartClient = cartClient;
        this.redisTemplate = redisTemplate;
    }

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
     * 주문 내역 상세
     */
    @Transactional(readOnly = true)
    public ApiResponse<?> getOrderProductList(Long orderId){
        Order order = orderRepository.getOrderProductsList(orderId);

        List<OrderProductDto> orderProductDtoList = orderProductDtoList(order);
        OrderListDto listDto = new OrderListDto(order,orderProductDtoList);

        return ApiResponse.success(listDto);
    }

    /**
     * 주문 내역 생성
     */
    @Transactional
    public ApiResponse<?> createOrder(long userId,OrderRequestDto orderRequestDto){
//        1. 상품 정보 가져오기
        ProductResponseDto responseDto = availableProducts(orderRequestDto.getProductId());

//        2.재고 파악
        Integer stock =redisTemplate.opsForValue().get(REDIS_STOCK_KEY + orderRequestDto.getProductId());
        if(stock == null || stock<orderRequestDto.getQuantity()){
            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
        }

//        3.주문테이블 생성
        Order order = new Order(userId);
        orderRepository.save(order);

//        4.주문제품 테이블 생성
        OrderProducts orderProducts = new OrderProducts(order,orderRequestDto,responseDto.getProductPrice());
        orderProductRepository.save(orderProducts);

//        5.총 주문 금액 계산
        BigDecimal totalPrice = orderProducts.getPrice().multiply(BigDecimal.valueOf(orderProducts.getQuantity()));

//        6. 주문테이블에 저장
        order.updatePrice(totalPrice);
        orderRepository.save(order);

        OrderListDto listDto = new OrderListDto(orderProducts,responseDto);
        return ApiResponse.success(listDto);

    }
    /**
     * 결제성공 -> 주문 성공
     */
    public void orderComplete(Long orderId){
        Order order = getOrderById(orderId);
        order.statusComplete();
        orderRepository.save(order);
    }

//    /**
//     * 상품 단건 구매
//     */
//    @Transactional
//    public ApiResponse<?> createOrder(OrderRequestDto dto){
//        BigDecimal totalPrice =BigDecimal.ZERO;
//
//        //1.주문 상태 생성
//        Order order = new Order(dto.getUserId());
//        orderRepository.save(order);
//        ProductResponseDto responseDto = productInfo(dto.getProductId());
//
//        productClient.decreaseStock(dto.getProductId(),dto.getQuantity());
//
////        2. 주문 처리
//        OrderProducts orderProduct = new OrderProducts(order,dto,responseDto);
//
//
//        totalPrice = totalPrice.add(orderProduct.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
//
//        // 3. 주문성공, 정보 저장
//        order.updateOrder(totalPrice);
//        orderRepository.save(order);
//        orderProductRepository.save(orderProduct);
//
//        OrderListDto listDto = new OrderListDto(orderProduct,responseDto);
//        return ApiResponse.success(listDto);
//
//    }


    /**
     *  장바구니 상품 구매 (다건 구매)
     */
    @Transactional
    public ApiResponse<?> createCartOrder(OrderListRequestDto dto){
        BigDecimal totalPrice =BigDecimal.ZERO;

//        1.주문 상태 생성
        Order order = new Order(dto.getUserId());
        orderRepository.save(order);

//        2. 각 상품들 주문테이블 저장
        for(Long cartProductId : dto.getCartProductList()){
            CartProductDto cartProduct = cartClient.cartProductDtoInfo(cartProductId);
            OrderProducts orderProduct = processOrderProduct(order,cartProduct.getProductId(),cartProduct.getQuantity());
            totalPrice = totalPrice.add(orderProduct.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity())));
            productClient.decreaseStock(cartProduct.getProductId(),cartProduct.getQuantity());
            orderProductRepository.save(orderProduct);
        }

//        3. 주문성공, 주문테이블 정보 저장
        order.updatePrice(totalPrice);
        orderRepository.save(order);

//        4. 주문한 상품 장바구니에서 삭제
        List<Long> cartProductId =dto.getCartProductList();
        cartClient.deleteCartProductList(cartProductId);

        List<OrderProducts> orderProductList = getOrderProductList(order);
        List<OrderProductDto> orderProductDtoList = orderProductList.stream().map(
                orderProducts -> {
                    ProductResponseDto productResponseDto = productInfo(orderProducts.getProductId());
                    return new OrderProductDto(orderProducts,productResponseDto);
                }).toList();
        OrderListDto listDto = new OrderListDto(order,orderProductDtoList);
        return ApiResponse.success(listDto);

    }



    /**
     * 결제 실패 -> 주문 실패
     */
    public void failOrder(Long orderId){
        Order order = getOrderById(orderId);
        order.statusFailed();
        orderRepository.save(order);
    }

    /**
     * 주문 취소 또는 반품시 상태변경
     */
    public ApiResponse<?> cancelOrder(Long orderId){
        Order order =orderRepository.findByOrderId(orderId).orElseThrow(()->new BusinessException(ErrorCode.ORDER_NOT_FOUND));
//        1. 취소 가능한 주문인지 확인
        if(order.getStatus()!= OrderStatus.COMPLETE && order.getStatus()!=OrderStatus.RETURN_COMPLETE){
            throw new BusinessException(ErrorCode.ORDER_CANCELLED_FAILED);
        }
//        2.제품 테이블 재고 반영
        for(OrderProducts orderProducts : order.getOrderProducts()){
           productClient.increaseStock(orderProducts.getProductId(),orderProducts.getQuantity());
        }
//        3.주문 테이블 상태, 취소로 변경
        order.statusCancel();

        orderRepository.save(order);

        log.info("상태변경{},날짜변경{}",order.getStatus(),order.getUpdatedAt());


        return ApiResponse.success(order.getStatus()+"처리 되었습니다.");
    }



//    주문시 주문상품테이블 정보저장
    private OrderProducts processOrderProduct(Order order,Long productId,Integer quantity){
        ProductResponseDto product = productInfo(productId);

//        주문수량보다 재고부족
        if(product.getQuantity()<quantity){
            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
        }

        OrderProducts orderProducts = new OrderProducts();
        orderProducts.saveOrderProducts(order,productId,quantity,product.getProductPrice());

        productClient.decreaseStock(productId,quantity); //재고감소
        orderProductRepository.save(orderProducts); //주문상품테이블 저장

        return orderProducts;
    }

    public Order getOrderById(Long orderId){
        return orderRepository.findByOrderId(orderId).orElseThrow(()->new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }
    public List<OrderProducts> getOrderProductList(Order order){
        return orderProductRepository.findByOrder(order);
    }

    //    주문상품테이블에서 상품이름이 추가된 dto
    private List<OrderProductDto> orderProductDtoList(Order order){
        return order.getOrderProducts().stream().map(

                orderProducts -> {
                    ProductResponseDto productResponseDto = productInfo(orderProducts.getProductId());
                    return new OrderProductDto(orderProducts,productResponseDto);
                }).toList();
    }

    //    상품정보
    private ProductResponseDto productInfo(Long productId){
        return productClient.information(productId);
    }
//    구매가능한 상품정보만
    private ProductResponseDto availableProducts(Long productId){
        return productClient.productStatus(productId);
    }
}
