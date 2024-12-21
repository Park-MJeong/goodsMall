package com.goodsmall.modules.product.controller;

import com.goodsmall.modules.product.dto.SliceProductDto;
import com.goodsmall.modules.product.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /*등록되어 있는 상품 리스트 조회*/
    @GetMapping("/show-list")
    public ResponseEntity<Slice<SliceProductDto>> getProducts(@RequestParam(value = "search")String search,
                                                              @RequestParam(value = "cursor",required = false)Integer cursor,
                                                              @RequestParam(value = "size",required = false)Integer size){
        if (cursor == null) {
            cursor = 0;
        }
        Slice<SliceProductDto> result = productService.getProductList(search, cursor, size);
        return ResponseEntity.ok(result);
    }
    /*상품상세정보*/
//    @GetMapping("{productId}/detail")
//    public ResponseEntity<ShowProductDto>getProductDetail(String productId ){
//        productService.getProductDetail(productId);
//        return ResponseEntity.ok().build();
//       }

}
