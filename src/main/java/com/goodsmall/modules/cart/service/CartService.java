package com.goodsmall.modules.cart.service;

import com.goodsmall.common.api.ApiResponse;
import com.goodsmall.common.constant.ErrorCode;
import com.goodsmall.common.exception.BusinessException;
import com.goodsmall.modules.cart.domain.CartProductRepository;
import com.goodsmall.modules.cart.domain.CartRepository;
import com.goodsmall.modules.cart.domain.entity.Cart;
import com.goodsmall.modules.cart.domain.entity.CartProducts;
import com.goodsmall.modules.cart.dto.*;
import com.goodsmall.modules.product.domain.Product;
import com.goodsmall.modules.product.domain.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j(topic = "장바구니 서비스")
@Service
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final EntityManager em;

    /**
     * 장바구니 내 상품리스트 조회 ,dto에 담아서 전달
     * */
    public ApiResponse<?> getCart(Long userId) {
        Cart cartList = getCartByUserId(userId);
        CartListDto listDto = new CartListDto(cartList);
        return ApiResponse.success(listDto);
    }

    /**
     * 장바구니 조회
     * 생성되지 않았으면 생성
     * */
    @Transactional
    public Cart getCartByUserId(Long userId){
        Cart cart = cartRepository.getCartByUserId(userId);
        if(cart ==null){
//            조회후 해당 유저의 장바구니 없으면 생성
            cart = new Cart(userId);
            cartRepository.save(cart);
            System.out.println("저장완료");
        }
        return cart;
    }

    /**
     * 장바구니  상품 추가
     * */
    @Transactional
    public ApiResponse<?> updateCart(Long userId, CartProductInfo dto){
//        1. 해당 유저 장바구니 조회
        Cart cart = getCartByUserId(userId);

        Product product =getProduct(dto.getProductId());

//        2. 오픈전 물건 장바구니에 저장 불가
        if(product.getStatus().equals("Pre-sale")){
            throw new BusinessException(ErrorCode.PRODUCT_PRE_SALE);
        }
//        3. 해당 물건이 장바구니에 있으면 중복저장 방지
        if(isProductAlreadyInCart(cart,product)){
            throw new BusinessException(ErrorCode.CART_PRODUCT_ALREADY);
        }
//        4.장바구니 제품테이블에 값 저장
        CartProducts cartProducts = new CartProducts(cart,product,dto.getQuantity());
        cartProductRepository.save(cartProducts);

        /**
         * 4. 레디스에 정보 업데이트
         * 추후 장바구니 제품 조회시간 비교하며 로직 진행
         * */
//        saveCartToRedis(userId,cart);

        CartProductDto cartProductDto = new CartProductDto(cartProducts);
        return ApiResponse.success(cartProductDto);
    }


    /**
     * 장바구니 내 상품정보 수정
     * */
    @Transactional
    public ApiResponse<?> updateProductQuantity(Long cartProductId,CartProductUpdateRequestDto dto) {
        Integer quantity = dto.getQuantity();

//        1. 장바구니 상품 테이블의 정보 조회
        CartProducts cartProduct = getCartProducts(cartProductId);
        Long productId = cartProduct.getProduct().getId();

        Cart cart = cartProduct.getCart();   //장바구니 정보, 수정 후 해당 장바구니 내 상품 리스트 출력위해

        System.out.println(dto.isDelete());
        if(dto.isDelete()){
            return deleteCartProduct(cart, cartProductId);
        }

        // 2. 해당 상품 재고 파악
        Product product = productRepository.findProductById(productId).orElseThrow(() ->
                new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
        );
//        2-1 품절상품이면 장바구니에서 삭제
        if(product.getStatus().equals("Sole Out")){
            deleteCartProduct(cart, cartProductId);
            return ApiResponse.success("품절된 상품입니다.");
        }

//        2-2. 변경 수량과 재고 비교
        if(product.getQuantity()<quantity){
            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
        }

        log.info("수량 변경 전  {}",cartProduct.getQuantity());
        //3. 수량변경
        cartProduct.setQuantity(quantity);
        cartProductRepository.save(cartProduct);

        log.info("수량 변경 후  {}",cartProduct.getQuantity());

        CartListDto listDto = new CartListDto(cart);
        return ApiResponse.success(listDto);
    }

    /**
     * 장바구니 상품 삭제
     * */

    @Transactional
    public ApiResponse<?> deleteCartProduct(Cart cart,Long cartProductId) {

        cartProductRepository.delete(cartProductId);
        em.flush();
        em.clear();

        Cart updateCart = getCartByUserId(cart.getUserId());
        System.out.println(updateCart.getCartProducts().size());
        if (updateCart ==null || updateCart.getCartProducts().isEmpty()) {
            return ApiResponse.success("장바구니가 비었습니다.");
        }
        CartListDto listDto = new CartListDto(updateCart);
        return ApiResponse.success(listDto);
    }

    /**
     * 주문완료된 상품 삭제
     * */

    public void deleteCartProductList(List<Long> cartProductId){
        cartProductId.forEach(cartProductRepository::delete);
        log.info("장바구니 상품 삭제 완료");
    }


    /**
     * 장바구니 상품 테이블의 정보 (개별 상품)
     * */
    private CartProducts getCartProducts(Long cartProductId){
        return cartProductRepository.getCartProducts(cartProductId).orElseThrow(
                ()->new BusinessException(ErrorCode.CART_PRODUCT_NOT_FOUND));
    }

    /**
     * 제품의 수량,품절체크 위한 조회
     * */
    private Product getProduct(Long productId) {
        return productRepository.getProduct(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private boolean isProductAlreadyInCart(Cart cart,Product product) {
        return cartProductRepository.isProductAlreadyInCart(cart,product);
    }

    public CartProductInfo getCartProductInformation(Long cartProductId) {
        return cartProductRepository.getCartProducts(cartProductId)
                .map(cartProduct -> new CartProductInfo(
                        cartProduct.getProduct().getId(),
                        cartProduct.getQuantity()
                ))
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_PRODUCT_NOT_FOUND));
    }

}
