package com.goodsmall.modules.order.service;

import com.goodsmall.common.api.ApiResponse;
import com.goodsmall.common.constant.ErrorCode;
import com.goodsmall.common.exception.BusinessException;
import com.goodsmall.modules.order.OrderStatus;
import com.goodsmall.modules.order.domain.OrderProductRepository;
import com.goodsmall.modules.order.domain.OrderRepository;
import com.goodsmall.modules.order.domain.entity.Order;
import com.goodsmall.modules.order.domain.entity.OrderProducts;
import com.goodsmall.modules.order.dto.OrderListDto;
import com.goodsmall.modules.order.dto.OrderListRequestDto;
import com.goodsmall.modules.order.dto.OrderProductDto;
import com.goodsmall.modules.order.dto.OrderRequestDto;
import com.goodsmall.modules.product.domain.Product;
import com.goodsmall.modules.product.domain.ProductRepository;
import com.goodsmall.modules.product.service.ProductService;
import com.goodsmall.modules.user.domain.User;
import com.goodsmall.modules.user.domain.UserRepository;
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
    private final UserRepository userRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;


    public OrderService(OrderRepository orderRepository, OrderProductRepository opRepository, UserRepository userRepository, ProductService productService, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.opRepository = opRepository;
        this.productService = productService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    //주문 내역 리스트
    public ApiResponse<?> getOrderList(Long userId,int pageNumber,int pageSize){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Order> orderList = orderRepository.getOrderList(userId,pageable);
        if(orderList.getTotalElements()==0){
            return ApiResponse.success("아직 주문내역이 존재하지 않습니다ㅜㅜ");
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Order order = new Order(user);
        //1.주문 상태 생성
        orderRepository.save(order);

        Product product = productRepository.getProduct(dto.getProductId()).orElseThrow(
                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

//        2. 제품 재고 확인 및 차감
        BigDecimal totalPrice =BigDecimal.ZERO;
        OrderProducts orderProduct = new OrderProducts(order,product,dto.getQuantity());

        productService.decreaseQuantity(dto);
        opRepository.save(orderProduct);
        totalPrice = totalPrice.add(product.getProductPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));

        // 3. 주문의 총 가격 업데이트
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.COMPLETE); // 상태 변경
        order.setUpdatedAt(LocalDateTime.now());
        OrderListDto listDto = new OrderListDto(orderProduct);
        System.out.println(listDto.getProducts());
        orderRepository.save(order);
        return ApiResponse.success(listDto);

    }

    @Transactional
    public ApiResponse<?> createCartOrder(Long userId, OrderListRequestDto dto){
        BigDecimal totalPrice =BigDecimal.ZERO;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        //1.주문 상태 생성
        Order order = new Order(user);

        orderRepository.save(order);
        for(OrderRequestDto products : dto.getProductList()){ //상품리스트안에서 각각 상품의 재고먼저확인
            Product product_information = productRepository.getProduct(products.getProductId()).orElseThrow(
                    () -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
            if (product_information.getQuantity() < products.getQuantity()) {
                throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
            }
            /**
             * 단건구매와 다시 로직이 같아지는데 어떻게 해야할지.........................
             * 값이 무조건누락될꺼같은데 처리로직에 대해서 도움을 요청합니다.*/
            OrderProducts orderProduct = new OrderProducts();
            orderProduct.setOrder(order);
            orderProduct.setProduct(product_information);
            orderProduct.setQuantity(products.getQuantity());
            orderProduct.setPrice(product_information.getProductPrice());
            log.info("orderProduct : {}", orderProduct.getProduct());


//            OrderProducts orderProduct = new OrderProducts(order,product_information,products);

            productService.decreaseQuantity(products);
            opRepository.save(orderProduct);
            log.info("saved order : {}", orderProduct.getProduct());
            totalPrice = totalPrice.add(product_information.getProductPrice().multiply(BigDecimal.valueOf(products.getQuantity())));

        }

        // 3. 주문의 총 가격 업데이트
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.COMPLETE); // 상태 변경
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        List<OrderProducts> orderProductList = opRepository.findByOrder(order);
        log.info("orderProductList : {}", orderProductList.size());
        List<OrderProductDto> orderProductDtoList = orderProductList.stream().map(OrderProductDto::new).toList();
        OrderListDto listDto = new OrderListDto(order,orderProductDtoList);
        return ApiResponse.success(listDto);

    }

//    주문 취소 ,반품
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



}
