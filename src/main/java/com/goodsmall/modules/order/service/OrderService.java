package com.goodsmall.modules.order.service;

import com.goodsmall.common.api.ApiResponse;
import com.goodsmall.common.constant.ErrorCode;
import com.goodsmall.common.exception.BusinessException;
import com.goodsmall.modules.cart.dto.CartProductInfo;
import com.goodsmall.modules.cart.service.CartService;
import com.goodsmall.modules.order.event.OrderStatus;
import com.goodsmall.modules.order.domain.OrderProductRepository;
import com.goodsmall.modules.order.domain.OrderRepository;
import com.goodsmall.modules.order.domain.entity.Order;
import com.goodsmall.modules.order.domain.entity.OrderProducts;
import com.goodsmall.modules.order.dto.OrderListDto;
import com.goodsmall.modules.order.dto.OrderListRequestDto;
import com.goodsmall.modules.order.dto.OrderProductDto;
import com.goodsmall.modules.order.dto.OrderRequestDto;
import com.goodsmall.modules.product.domain.Product;
import com.goodsmall.modules.product.service.ProductService;
import com.goodsmall.modules.user.domain.User;
import com.goodsmall.modules.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository opRepository;
    private final ProductService productService;
    private final CartService cartService;
    private final UserService userService;


    public OrderService(OrderRepository orderRepository, OrderProductRepository opRepository, ProductService productService, CartService cartService, UserService userService) {
        this.orderRepository = orderRepository;
        this.opRepository = opRepository;
        this.productService = productService;
        this.cartService = cartService;
        this.userService = userService;
    }

    //주문 내역 리스트
    public ApiResponse<?> getOrderList(Long userId,int pageNumber,int pageSize){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Order> orderList = orderRepository.getOrderList(userId,pageable);
        if(orderList.getTotalElements()==0){
            return ApiResponse.success("아직 주문내역이 존재하지 않습니다.");
        }

        List<OrderListDto> listDto = orderList.getContent().stream()
                .map(OrderListDto::new)
                .collect(Collectors.toList());

        return ApiResponse.success(listDto);
    }

    //주문 내역 상세 물품
    public ApiResponse<?> getOrderProductList(Long orderId){
        Order orderProductList = orderRepository.getOrderProductsList(orderId);
        OrderListDto listDto = new OrderListDto(orderProductList);

        return ApiResponse.success(listDto);
    }


    //상품 단건 구매
    @Transactional
    public ApiResponse<?> createOrder(Long userId,OrderRequestDto dto){
        BigDecimal totalPrice =BigDecimal.ZERO;

        User user=findUser(userId);
        //1.주문 상태 생성
        Order order =initializeOrder(user);

//        2. 주문 처리
        OrderProducts orderProduct = processOrderProduct(order,dto.getProductId(),dto.getQuantity());


        totalPrice = totalPrice.add(orderProduct.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));

        // 3. 주문의 총 가격 업데이트
        finalizeOrder(order,totalPrice);

        OrderListDto listDto = new OrderListDto(orderProduct);
        return ApiResponse.success(listDto);

    }

//    장바구니상품 다건 구매
    @Transactional
    public ApiResponse<?> createCartOrder(Long userId, OrderListRequestDto dto){
        BigDecimal totalPrice =BigDecimal.ZERO;

        User user = findUser(userId);
        //1.주문 상태 생성
        Order order =initializeOrder(user);


        for(Long cartProductId : dto.getCartProductList()){
            CartProductInfo requestDto = getCartProductInfo(cartProductId);
            OrderProducts orderProduct = processOrderProduct(order,requestDto.getProductId(),requestDto.getQuantity());

            log.info("orderProduct : {}", orderProduct.getProduct());

            totalPrice = totalPrice.add(orderProduct.getPrice().multiply(BigDecimal.valueOf(requestDto.getQuantity())));

        }

        // 3. 주문정보 저장
        finalizeOrder(order,totalPrice);

//        4. 주문한 장바구니 상품 삭제
        List<Long> cartProductId =dto.getCartProductList();
        cartService.deleteCartProductList(cartProductId);

        List<OrderProducts> orderProductList = opRepository.findByOrder(order);
        log.info("orderProductList : {}", orderProductList.size());
        List<OrderProductDto> orderProductDtoList = orderProductList.stream().map(OrderProductDto::new).toList();
        OrderListDto listDto = new OrderListDto(order,orderProductDtoList);
        return ApiResponse.success(listDto);

    }


    /**
     * 주문 취소 또는 반품시 상태변경
     */
    public ApiResponse<?> cancelOrder(Long orderId){
        Order order =orderRepository.findByOrderId(orderId).orElseThrow(()->new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        if(order.getStatus()!=OrderStatus.COMPLETE && order.getStatus()!=OrderStatus.RETURN_COMPLETE){
            throw new BusinessException(ErrorCode.ORDER_CANCELLED_FAILED);
        }
        productService.updateProductQuantities(order); //재고반영

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        log.info("상태변경{},날짜변경{}",order.getStatus(),order.getUpdatedAt());


        return ApiResponse.success(order.getStatus()+"처리 되었습니다.");
    }

    /**
     * CartProductInfo 추출 및 데이터 반환
     */
    private CartProductInfo getCartProductInfo(Long cartProductId) {
        return cartService.getCartProductInformation(cartProductId);
    }
    /**
     * 유저정보 확인
     */
    private User findUser(Long userId){
        return userService.findUser(userId);
    }
    /**
     * 제품정보 확인
     */
    private Product getProduct(Long productId){
        return productService.getProduct(productId);
    }
    /**
     *  주문정보 생성
     */
    private Order initializeOrder(User user) {
        Order order = new Order(user);
        orderRepository.save(order);
        return order;
    }
    /**
     *  주문처리
     */
    private OrderProducts processOrderProduct(Order order,Long productId,Integer quantity){
        Product product_information = getProduct(productId);
        if(product_information.getQuantity()<quantity){
            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
        }
//        주문상품 테이블
        OrderProducts orderProducts = new OrderProducts(order,product_information,quantity);

        productService.decreaseQuantity(quantity,productId); //재고감소
        opRepository.save(orderProducts);

        return orderProducts;
    }
    /**
     *  주문정보 저장
     */
    private void finalizeOrder(Order order,BigDecimal totalPrice) {
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.COMPLETE);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }


}
