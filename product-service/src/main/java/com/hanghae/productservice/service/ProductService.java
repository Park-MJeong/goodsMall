package com.hanghae.productservice.service;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.productservice.util.SliceUtil;
import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductRepository;
import com.hanghae.productservice.dto.ProductDto;
import com.hanghae.productservice.dto.SliceProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Product-Service-controller")
public class ProductService {
    private final ProductRepository repository;

    private static final String REDIS_STOCK_KEY = "product:stock:";
    private final RedisTemplate<String,Integer> redisTemplate;

    /**
     * 공통 :제품정보 제공
     */

    public Product getProduct(Long id){
        Product product =repository.findProductById(id).orElseThrow(
                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        switch (product.getStatus()) {
            case "Sold Out":
                throw new BusinessException(ErrorCode.PRODUCT_SOLD_OUT);
            case "Pre-sale":
                throw new BusinessException(ErrorCode.PRODUCT_PRE_SALE);
            default:
                return product;
        }
    }

    /**
     * 공통 :제품정보 제공 (상태 예외처리 없이 전달)
     */
    @Cacheable(value = "productDetails", key = "#id")
    public Product getProductAll(Long id){
        return repository.findProductById(id).orElseThrow(
                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }


    /**
     * 공통 :제품수량체크
     */
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
    @Transactional(readOnly = true)
    public ApiResponse<?> getProductList(String search, Long cursor, Integer size){
        int limitSize = SliceUtil.sliceSize(size);
        List<Product> products = repository.getProductList(search,cursor, Pageable.ofSize(limitSize));
        List<SliceProductDto> productDtos = products.stream()
                .map(SliceProductDto::fromProductDto)
                .collect(Collectors.toList());

        Slice<SliceProductDto> showList = SliceUtil.getSlice(productDtos,size);

        if (showList.isEmpty()) {
            return ApiResponse.success(SliceUtil.getSlice(
                    List.of(new SliceProductDto("더 이상 상품이 존재하지 않습니다.")), size));
        }

        return ApiResponse.success(showList);
    }

    /**
     * 제품 상세 페이지
     */
    @Transactional(readOnly = true)
    public ProductDto getProductDto(Long id) {
        Product product = getProductAll(id);

//        상세페이지 들어왔다는것은 살 확률 있음. 레디스에 재고넣어줌
        String key = REDIS_STOCK_KEY + product.getId();
        if(redisTemplate.opsForValue().get(key) ==null){
            redisTemplate.opsForValue().set(key,product.getQuantity(), Duration.ofDays(1));
        }
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