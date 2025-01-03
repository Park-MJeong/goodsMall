package com.hanghae.productservice.controller;

import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.dto.ProductDto;
import com.hanghae.productservice.dto.SliceProductDto;
import com.hanghae.productservice.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /*등록되어 있는 상품 리스트 조회*/
    @GetMapping("/")
    public ResponseEntity<Slice<SliceProductDto>> getProducts(@RequestParam(value = "search")String search,
                                                              @RequestParam(value = "cursor",required = false)Long cursor,
                                                              @RequestParam(value = "size",required = false)Integer size){
        if (cursor == null) {
            cursor = 0L;
        }
        Slice<SliceProductDto> result = productService.getProductList(search, cursor, size);
        return ResponseEntity.ok(result);
    }
    /*상품상세정보*/
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto>getProduct(@PathVariable Long productId ){
        return ResponseEntity.ok(productService.getProductDto(productId));
       }

    @GetMapping("/information/{productId}")
    public Product information(@PathVariable Long productId){
        return productService.getProduct(productId);
    }

    @GetMapping("/productQuantity/{productId}")
    public Product getProductQuantity(@PathVariable Long productId){
        return productService.getProductQuantity(productId);
    }

    @PostMapping("/decreaseStock")
    public void decreaseStock(@RequestParam Long productId,@RequestParam Integer quantity){
        productService.decreaseStock(productId,quantity);
    }
    @PostMapping("/increaseStock")
    public void increaseStock(@RequestParam Long productId,@RequestParam Integer quantity){
        productService.increaseStock(productId,quantity);
    }

}
