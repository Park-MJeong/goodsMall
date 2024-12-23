package com.goodsmall.modules.cart.controller;

import com.goodsmall.common.api.ApiResponse;
import com.goodsmall.modules.cart.dto.CartProductUpdateRequestDto;
import com.goodsmall.modules.cart.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }


    /**
     * 장바구니 상품 조회
     * GET /api/carts/{cartId}
     * @param cartId   장바구니Id
     */

    @GetMapping("/{cartId}")
    public ResponseEntity<ApiResponse<?>> getCart(@PathVariable Long cartId){
        ApiResponse<?> response = cartService.getCart(cartId);
        return ResponseEntity.ok(response);
    }

//    /**
//     * 장바구니 상품 추가
//     * GET /api/carts/{cartId}
//     * @param cartId   장바구니Id
//     */
//    @PostMapping("/{cartId}")
//    public ResponseEntity<ApiResponse<?>> updateCart(@PathVariable Long cartId){
//        ApiResponse<?> response = cartService.updateCart(cartId);
//        return ResponseEntity.ok(response);
//    }


    /**
     * 장바구니 상품 수량 수정
     * PUT /api/carts/{cartId}/products/{productId}
     * @param cartProductId 장바구니상품 ID
     * @param dto 원하는 상품 수량과 삭제여부
     * @return 수량 수정 결과
     */

    @PutMapping("/{cartProductId}")
    public ResponseEntity<ApiResponse<?>> updateProductQuantity(@PathVariable Long cartProductId,
                                                                @RequestBody CartProductUpdateRequestDto dto) {
        ApiResponse<?> response = cartService.updateProductQuantity(cartProductId, dto);
        return ResponseEntity.ok(response);
    }
}
