package com.hanghae.cartservice.controller;

import com.hanghae.cartservice.dto.CartProductDto;
import com.hanghae.cartservice.dto.CartProductInfo;
import com.hanghae.cartservice.dto.CartProductUpdateRequestDto;
import com.hanghae.cartservice.service.CartService;
import com.hanghae.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    public ResponseEntity<ApiResponse<?>> getCart(HttpServletRequest request){
        long userId=Long.parseLong(request.getParameter("X-Claim-userId"));
        ApiResponse<?> response = cartService.getCart(userId);
        return ResponseEntity.ok(response);
    }
    /**
     * 장바구니 상품 추가
     * POST /api/carts/
     * @param dto 원하는 상품의 아이디와 정보
     * @return 장바구니에 추가된 상품 정보
     */
    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> updateCart(@RequestBody @Valid CartProductInfo dto){
        ApiResponse<?> response = cartService.updateCart(dto);
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

    @GetMapping("/cartProductDto/{cartProductId}")
    public CartProductDto cartProductDtoInfo(@PathVariable Long cartProductId){
        return cartService.getCartProductInformation(cartProductId);
    }

    @GetMapping("/deleteCartProductList")
    public void deleteCartProductList(@RequestParam List<Long> cartProductId){
        cartService.deleteCartProductList(cartProductId);
    }

}
