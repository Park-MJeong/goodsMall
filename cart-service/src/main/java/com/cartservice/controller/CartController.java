package com.cartservice.controller;

import com.goodsmall.common.api.ApiResponse;
import com.goodsmall.common.security.jwt.CustomUserDetails;
import com.goodsmall.modules.cart.dto.CartProductInfo;
import com.goodsmall.modules.cart.dto.CartProductUpdateRequestDto;
import com.goodsmall.modules.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
     * GET /api/carts/
     */

    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> getCart(@AuthenticationPrincipal CustomUserDetails userDetails){
        ApiResponse<?> response = cartService.getCart(userDetails.getId());
        return ResponseEntity.ok(response);
    }
    /**
     * 장바구니 상품 추가
     * POST /api/carts/
     * @param dto 원하는 상품의 아이디와 정보
     * @return 장바구니에 추가된 상품 정보
     */
    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> updateCart(@RequestBody @Valid CartProductInfo dto, @AuthenticationPrincipal CustomUserDetails userDetails){
        ApiResponse<?> response = cartService.updateCart(userDetails.getId(),dto);
        return ResponseEntity.ok(response);
    }

    /**
     * 장바구니 상품 수량 수정
     * PUT /api/carts
     * @param dto 원하는 상품 수량과 삭제여부
     * @return 수량 수정 결과
     */

    @PutMapping("/product/{cartProductId}")
    public ResponseEntity<ApiResponse<?>> updateProductQuantity(@PathVariable Long cartProductId,@RequestBody CartProductUpdateRequestDto dto) {
        ApiResponse<?> response = cartService.updateProductQuantity(cartProductId, dto);
        return ResponseEntity.ok(response);
    }
}
