package com.hanghae.productservice.service;

import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.util.SliceUtil;
import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductRepository;
import com.hanghae.productservice.dto.ProductDto;
import com.hanghae.productservice.dto.SliceProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Product-Service-controller")
public class ProductService {
    private final ProductRepository repository;

    //   공통: 제품 정보 조회
    public Product getProduct(Long id){
        Product product =repository.findProductById(id).orElseThrow(
                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        if(product.getStatus().equals("Sold Out")){
            throw new BusinessException(ErrorCode.PRODUCT_SOLD_OUT);
        }
        if(product.getStatus().equals("Pre-sale")){
            throw new BusinessException(ErrorCode.PRODUCT_PRE_SALE);
        }
        return product;
    }

    //  제품 수량 및 상태 조회
    public Product getProductQuantity(Long id){
        return repository.findProductById(id).orElseThrow(
                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }


    //   공통: 제품 수량 체크
    public Product checkStock(Long productId, int quantity) {
        Product product = getProduct(productId);
        if (product.getQuantity() < quantity) {
            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
        }
        return product;
    }

    /**
     * 전체 상품 조회
     */
    public Slice<SliceProductDto> getProductList(String search, Long cursor, Integer size){
        int limitSize = SliceUtil.sliceSize(size);
        List<Product> products = repository.getProductList(search,cursor, Pageable.ofSize(limitSize));
        List<SliceProductDto> productDtos = products.stream()
                .map(SliceProductDto::fromProductDto)
                .collect(Collectors.toList());

        Slice<SliceProductDto> showList = SliceUtil.getSlice(productDtos,size);

        if (showList.isEmpty()) {
            return SliceUtil.getSlice(
                    List.of(new SliceProductDto("더 이상 상품이 존재하지 않습니다.")), size);
        }

        return showList;
    }

    /**
     * 제품 상세 페이지
     */
    public ProductDto getProductDto(Long id) {
        Product product = getProduct(id);
        return new ProductDto(product);
    }


//    제품 구매시 재고감소
    @Transactional
    public void decreaseStock(Long productId,Integer quantity){

        Product product = checkStock(productId, quantity);
        log.info("감소 전{}",product.getQuantity());

        product.decreaseQuantity(quantity);
        log.info("감소 후{}",product.getQuantity());
        repository.save(product);
    }
// 주문 취소 시 재고 반영
    @Transactional
    public void increaseStock(Long productId,Integer quantity){
        Product product = checkStock(productId, quantity);
        log.info("반영 전{}",product.getQuantity());

        product.increaseQuantity(quantity);
        log.info("반영 후{}",product.getQuantity());

        repository.save(product);
    }




}