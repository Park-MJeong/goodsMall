package com.hanghae.cartservice.service;

import com.hanghae.cartservice.client.ProductClient;
import com.hanghae.cartservice.client.dto.ProductResponseDto;
import com.hanghae.cartservice.domain.CartProductRepository;
import com.hanghae.cartservice.domain.CartRepository;
import com.hanghae.cartservice.domain.entity.Cart;
import com.hanghae.cartservice.domain.entity.CartProducts;
import com.hanghae.cartservice.dto.CartListDto;
import com.hanghae.cartservice.dto.CartProductDto;
import com.hanghae.cartservice.dto.CartProductInfo;
import com.hanghae.cartservice.dto.CartProductUpdateRequestDto;
import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.exception.BusinessException;
import jakarta.persistence.EntityManager;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j(topic = "cart-service-controller")
@Service
@AllArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final EntityManager em;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductClient productClient;

    /**
     * 장바구니 내 상품리스트 조회 ,dto에 담아서 전달
     * */

    @Transactional
    public ApiResponse<?> getCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        if (cart.getCartProducts()==null) {
            return ApiResponse.success("장바구니에 담긴 상품이 없습니다.");
        }
        CartListDto listDto = createCartListDto(cart);
        return ApiResponse.success(listDto);
    }

    /**
     * 장바구니 유무조회
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
    public ApiResponse<?> updateCart(CartProductInfo dto,long userId){
//        1. 해당 유저 장바구니 조회
        Cart cart = getCartByUserId(userId);
        ProductResponseDto product = productInfo(dto.getProductId());

//        3. 해당 물건이 장바구니에 있으면 중복저장 방지
        if(isProductAlreadyInCart(cart,dto.getProductId())){
            throw new BusinessException(ErrorCode.CART_PRODUCT_ALREADY);
        }
//        4.장바구니 제품테이블에 값 저장
        CartProducts cartProducts = new CartProducts(cart,dto);
        cartProductRepository.save(cartProducts);

        /**
         * 4. 레디스에 정보 업데이트
         * 추후 장바구니 제품 조회시간 비교하며 로직 진행
         * */
//        saveCartToRedis(userId,cart);

        CartProductDto cartProductDto = new CartProductDto(cartProducts,product);
        return ApiResponse.success(cartProductDto);
    }


    /**
     * 장바구니 내 상품정보 수정
     * */
    @Transactional
    public ApiResponse<?> updateProductQuantity(Long cartProductId, CartProductUpdateRequestDto dto) {
        Integer quantity = dto.getQuantity();

//        1. 장바구니 상품 테이블의 정보 조회
        CartProducts cartProduct = getCartProducts(cartProductId);

        Cart cart = cartProduct.getCart();   //장바구니 정보, 수정 후 해당 장바구니 내 상품 리스트 출력위해

        if(dto.isDelete()){
            return deleteCartProduct(cart, cartProductId);
        }

        // 2. 해당 상품 재고 파악
        ProductResponseDto product = productClient.getProductQuantity(cartProduct.getProductId());

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

        List<CartProductDto> listDto = cartProductList(cart);
        return ApiResponse.success(listDto);
    }

    /**
     * 장바구니 상품 삭제, 남은 정보 dto에 담아서 전달
     * */
    @Transactional
    public ApiResponse<?> deleteCartProduct(Cart cart,Long cartProductId) {

        cartProductRepository.delete(cartProductId);
        em.flush();
        em.clear();

        Cart updateCart = getCartByUserId(cart.getUserId());
        if (updateCart ==null || updateCart.getCartProducts().isEmpty()) {
            return ApiResponse.success("장바구니가 비었습니다.");
        }
        List<CartProductDto> listDto = cartProductList(updateCart);
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
     * 장바구니에 존재하는 상품인지
     * */
    private boolean isProductAlreadyInCart(Cart cart,Long productId) {
        return cartProductRepository.isProductAlreadyInCart(cart,productId);
    }

    /**
     * CartProductInfo 추출 및 데이터 반환
     */

    public CartProductDto getCartProductInformation(Long cartProductId) {
        CartProducts cartProducts = getCartProducts(cartProductId);
        ProductResponseDto product=productInfo(cartProducts.getProductId());
        return new CartProductDto(cartProducts,product);
    }

    /**
     * 공통 처리: 장바구니 DTO 생성
     */
    private CartListDto createCartListDto(Cart cart) {
        List<CartProductDto> cartProductDtoList = cartProductList(cart);
        return new CartListDto(cart, cartProductDtoList);
    }

    /**
     * 공통 처리: 장바구니 상품 리스트를 DTO 리스트로 변환
     */
    private List<CartProductDto> cartProductList(Cart cart){
        return cart.getCartProducts().stream().map(
                cartProducts -> {
                    ProductResponseDto productResponseDto = productInfo(cartProducts.getProductId());
                    return new CartProductDto(cartProducts,productResponseDto);
                }).toList();
    }


    /**
     * 공통 처리: 상품정보 조회
     */
    private ProductResponseDto productInfo(Long productId){
        return productClient.information(productId);
    }
}
