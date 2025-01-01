package com.hanghae.orderservice.service;


import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.constant.ErrorCode;
import com.hanghae.common.dto.ProductResponseDto;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.orderservice.client.ProductClient;
import com.hanghae.orderservice.domain.OrderProductRepository;
import com.hanghae.orderservice.domain.OrderRepository;
import com.hanghae.orderservice.domain.entity.Order;
import com.hanghae.orderservice.domain.entity.OrderProducts;
import com.hanghae.orderservice.dto.OrderListDto;
import com.hanghae.orderservice.dto.OrderProductDto;
import com.hanghae.orderservice.dto.OrderRequestDto;
import com.hanghae.orderservice.event.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;

    private final ProductClient productClient;


    /**
     * 주문리스트 조회
     */
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
//                    3. 해당 주문의 물품리스트정보
                    List<OrderProductDto> orderProductDtoList = orderProductDtoList(order);
                return new OrderListDto(order,orderProductDtoList);}).toList();

        return ApiResponse.success(listDto);
    }

    /**
     * 개별 주문 정보
     */
    public ApiResponse<?> getOrderProductList(Long orderId){
        Order order = orderRepository.getOrderProductsList(orderId);

        List<OrderProductDto> orderProductDtoList = orderProductDtoList(order);
        OrderListDto listDto = new OrderListDto(order,orderProductDtoList);

        return ApiResponse.success(listDto);
    }

    /**
     * 상품 단건 구매
     */
    @Transactional
    public ApiResponse<?> createOrder(OrderRequestDto dto){
        BigDecimal totalPrice =BigDecimal.ZERO;

        //1.주문 상태 생성
        Order order = new Order(dto.getUserId());
        orderRepository.save(order);
        ProductResponseDto responseDto = productInfo(dto.getProductId());

        productClient.decreaseStock(dto.getProductId(),dto.getQuantity());

//        2. 주문 처리
        OrderProducts orderProduct = new OrderProducts(order,dto,responseDto);


        totalPrice = totalPrice.add(orderProduct.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));

        // 3. 주문성공, 정보 저장
        order.updateOrder(totalPrice);
        orderRepository.save(order);
        orderProductRepository.save(orderProduct);

        OrderListDto listDto = new OrderListDto(orderProduct,responseDto);
        return ApiResponse.success(listDto);

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
        order.statusChanged();

        orderRepository.save(order);

        log.info("상태변경{},날짜변경{}",order.getStatus(),order.getUpdatedAt());


        return ApiResponse.success(order.getStatus()+"처리 되었습니다.");
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

    //    장바구니상품 다건 구매
//    @Transactional
//    public ApiResponse<?> createCartOrder(OrderListRequestDto dto){
//        BigDecimal totalPrice =BigDecimal.ZERO;
//
//        //1.주문 상태 생성
//        Order order = new Order(dto.getUserId());
//
//
//        for(Long cartProductId : dto.getCartProductList()){
//            CartProductInfo requestDto = getCartProductInfo(cartProductId);
//            OrderProducts orderProduct = processOrderProduct(order,requestDto.getProductId(),requestDto.getQuantity());
//
//            log.info("orderProduct : {}", orderProduct.getProduct());
//
//            totalPrice = totalPrice.add(orderProduct.getPrice().multiply(BigDecimal.valueOf(requestDto.getQuantity())));
//
//        }
//
//        // 3. 주문성공, 정보 저장
//        order.updateOrder(totalPrice);
//        orderRepository.save(order);
//        orderProductRepository.save(orderProduct);
//
////        4. 주문한 장바구니 상품 삭제
//        List<Long> cartProductId =dto.getCartProductList();
//        cartService.deleteCartProductList(cartProductId);
//
//        List<OrderProducts> orderProductList = opRepository.findByOrder(order);
//        log.info("orderProductList : {}", orderProductList.size());
//        List<OrderProductDto> orderProductDtoList = orderProductList.stream().map(OrderProductDto::new).toList();
//        OrderListDto listDto = new OrderListDto(order,orderProductDtoList);
//        return ApiResponse.success(listDto);
//
//    }



    //    /**
//     * CartProductInfo 추출 및 데이터 반환
//     */
//    private CartProductInfo getCartProductInfo(Long cartProductId) {
//        return cartService.getCartProductInformation(cartProductId);
//    }


//    /**
//     *  주문처리
//     */
//    private OrderProducts processOrderProduct(Order order,Long productId,Integer quantity){
//        Product product_information = getProduct(productId);
//        if(product_information.getQuantity()<quantity){
//            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
//        }
////        주문상품 테이블
//        OrderProducts orderProducts = new OrderProducts(order,productId,quantity);
//
//        productClient.decreaseStock(productId,quantity); //재고감소
//        opRepository.save(orderProducts);
//
//        return orderProducts;
//    }
}
