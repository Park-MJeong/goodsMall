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
    private final OrderRepository oRepository;
    private final OrderProductRepository opRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository oRepository, OrderProductRepository opRepository, UserRepository userRepository, ProductService productService, ProductRepository productRepository) {
        this.oRepository = oRepository;
        this.opRepository = opRepository;
        this.productService = productService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    //주문 내역 리스트
    public ApiResponse<?> getOrderList(Long userId,int pageNumber,int pageSize){
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Order> orderList = oRepository.getOrderList(userId,pageable);

        List<OrderListDto> listDto = orderList.getContent().stream()
                .map(OrderListDto::new)
                .collect(Collectors.toList());

        return ApiResponse.success(listDto);
    }

    //주문 내역 상세 물품
    public ApiResponse<?> getOrderProductList(Long orderId){
        Order orderProductList = oRepository.getOrderProductsList(orderId);
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
        oRepository.save(order);

        Product product = productRepository.getProduct(dto.getProductId()).orElseThrow(
                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

//        2. 제품 재고 확인 및 차감
        BigDecimal totalPrice =BigDecimal.ZERO;
        OrderProducts orderProduct = new OrderProducts(order,product,dto.getQuantity());

        if(productService.getProduct(dto.getProductId())!=null){
            productService.decreaseQuantity(dto);

            opRepository.save(orderProduct);
            totalPrice = totalPrice.add(product.getProductPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        }
        // 3. 주문의 총 가격 업데이트
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.COMPLETE); // 상태 변경
        order.setUpdatedAt(LocalDateTime.now());
        OrderListDto listDto = new OrderListDto(orderProduct);
        System.out.println(listDto.getProducts());
        oRepository.save(order);
        return ApiResponse.success(listDto);

    }
    public ApiResponse<?> createCartOrder(Long cartId, OrderListRequestDto dto){
        return null;

    }

    public ApiResponse<?> cancelOrder(Long orderId){
        return null;
    }



}
