package com.cartservice.controller;


import com.cartservice.dto.CartProductAddRequestDto;
import com.cartservice.dto.CartProductUpdateRequestDto;
import com.cartservice.service.CartService;
import com.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

//    @GetMapping("/{cartId}")
//    public ResponseEntity<ApiResponse<?>> getCart(@PathVariable Long cartId){
//        ApiResponse<?> response = cartService.getCart(cartId);
//        return ResponseEntity.ok(response);
//    }
    /**
     * 장바구니 상품 추가
     * POST /api/carts/{cartId}
     * @param cartId   장바구니Id
     * @param dto 원하는 상품의 아이디와 정보
     * @return 장바구니에 추가된 상품 정보
     */
//    @PostMapping("/{cartId}")
//    public ResponseEntity<ApiResponse<?>> updateCart(@PathVariable Long cartId, @RequestBody @Valid CartProductAddRequestDto dto){
//        ApiResponse<?> response = cartService.updateCart(cartId,dto);
//        return ResponseEntity.ok(response);
//    }

    /**
     * 장바구니 상품 수량 수정
     * PUT /api/carts/{cartProductId}
     * @param cartProductId 장바구니상품 ID
     * @param dto 원하는 상품 수량과 삭제여부
     * @return 수량 수정 결과
     */
//
//    @PutMapping("/product/{cartProductId}")
//    public ResponseEntity<ApiResponse<?>> updateProductQuantity(@PathVariable Long cartProductId,
//                                                                @RequestBody CartProductUpdateRequestDto dto) {
//        ApiResponse<?> response = cartService.updateProductQuantity(cartProductId, dto);
//        return ResponseEntity.ok(response);
//    }
}
