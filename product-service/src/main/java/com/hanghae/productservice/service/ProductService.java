package com.hanghae.productservice.service;

import com.hanghae.common.api.ApiResponse;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.exception.BusinessException;
import com.hanghae.common.kafka.OrderEvent;
import com.hanghae.common.kafka.OrderRequestDto;
import com.hanghae.productservice.domain.ProductStatus;
import com.hanghae.productservice.dto.ProductIdAndQuantityDto;
import com.hanghae.productservice.util.SliceUtil;
import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductRepository;
import com.hanghae.productservice.dto.ProductDto;
import com.hanghae.productservice.dto.SliceProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hanghae.common.util.RedisKeyUtil.*;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Product-Service-controller")
public class ProductService {
    private final ProductRepository repository;
    private final CacheableProductService cacheableProductService;
    private final RedisTemplate<String,Integer> redisTemplate;

    /**
     * 공통 :제품정보 제공
     */
    public Product getProduct(Long id){
        Product product =repository.findProductById(id).orElseThrow(
                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        switch (product.getStatus()) {
            case SOLD_OUT:
                throw new BusinessException(ErrorCode.PRODUCT_SOLD_OUT);
            case PRE_SALE:
                throw new BusinessException(ErrorCode.PRODUCT_PRE_SALE);
            default:
                return product;
        }
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
        Product product = cacheableProductService.getProductAll(id);

//        상세페이지 들어왔다는것은 살 확률 있음. 레디스에 재고넣어줌
        setRedisStock(product);
        return new ProductDto(product);
    }


//    제품 구매성공 시 레디스 재고 반영
    @Transactional
    public void decreaseStock(OrderEvent orderEvent){
        List<OrderRequestDto> orderRequestDtoList = orderEvent.getOrderRequestDtoList();

        // 1. 업데이트된 상품을 저장할 리스트
        List<Product> updatedProducts = orderRequestDtoList.stream()
                .map(this::updateProductStock)
                .toList();

//        2. 데이터베이스에 일괄 저장
        repository.saveAll(updatedProducts);

        log.info("재고 반영 완료: {}", updatedProducts.stream()
                .map(product -> "상품 ID: " + product.getId() + ", 남은 재고: " + product.getQuantity())
                .toList());
    }

    private Product updateProductStock(OrderRequestDto orderRequestDto) {
        Long productId = orderRequestDto.getProductId();

        // 1. Redis에서 재고 정보 가져오기
        Integer stock = getRedisStock(productId);
        System.out.println("레디스에서 재고 정보 가져오기 : "+stock);

        // 2. 데이터베이스에서 상품 정보 가져오기
        Product product = getProduct(productId);

        // 3. 상품 상태 업데이트
        if(stock <=0){
            product.toBuilder()
                    .quantity(stock)
                    .status(ProductStatus.SOLD_OUT)
                    .build();
        }

        return product.toBuilder()
                .quantity(stock)
                .build();
    }

// 주문 취소 시 재고 반영
//    @Transactional
//    public void increaseStock(ProductIdAndQuantityDto productIdAndQuantityDto){
//
//        Product product = cacheableProductService.getProductAll(productIdAndQuantityDto.getProductId());
//        log.info("재고 증가 처리 - 반영 전 재고: {}, 상품 ID: {}", product.getQuantity(), product.getId());
//
//        cacheableProductService.updateProduct(productIdAndQuantityDto);
//        log.info("반영 후{}",product.getQuantity());
//    }
    @Transactional
    public void increaseStock(ProductIdAndQuantityDto productIdAndQuantityDto){

        Product product = cacheableProductService.getProductAll(productIdAndQuantityDto.getProductId());
        log.info("재고 증가 처리 - 반영 전 재고: {}, 상품 ID: {}", product.getQuantity(), product.getId());
        if(product.getStatus()==ProductStatus.SOLD_OUT){
            product.toBuilder()
                    .quantity(productIdAndQuantityDto.getQuantity()+ product.getQuantity())
                    .status(ProductStatus.ON_SALE)
                    .build();
        }else {
            product.toBuilder()
                    .quantity(productIdAndQuantityDto.getQuantity()+ product.getQuantity())
                    .build();
        }
        repository.save(product);
        cacheableProductService.refreshProductCache(product.getId());
        log.info("반영 후{}",product.getQuantity());
    }

    /**
     * 상품 오픈하기
     */
    public Product isAvailableProducts(Long productId){
        log.info("[상품오픈하기]: 상품조회 진입");
//        1. 상품조회
        Product product = cacheableProductService.getProductAll(productId);
//        2. 품절이거나 판매준비중 상품여부 확인
        validateProductTime(product);
//        3. 오픈 상품 => 상태변경
        if(product.getStatus()!=ProductStatus.ON_SALE){
            changeStatus(product,ProductStatus.ON_SALE);
        }
        if(redisTemplate.opsForValue().get(getStockKey(productId))==null){
            //        4.레디스 재고 저장
            setRedisStock(product);
        }

        return product;
    }


////    제품 수량 체크
//    private Product checkStock(Long productId, int quantity) {
//        Product product = getProduct(productId);
//        if (product.getQuantity() < quantity) {
//            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT,"재고 부족한 제품: "+productId);
//        }
//        return product;
//    }

//   품절이거나 판매준비중 상품여부 확인
    private void validateProductTime(Product product) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime openTime = product.getOpenDate();
        if(product.getStatus().equals(ProductStatus.SOLD_OUT)||!now.isAfter(openTime)){
            throw new BusinessException(ErrorCode.PRODUCT_NOT_ORDER,"주문 불가능한 상품: " + product.getId());
        }
    }

//    레디스 정보 저장
    private void setRedisStock(Product product){
        String stockKey = getStockKey(product.getId());
        String productKey = getProductKey(product.getId());
        if(redisTemplate.opsForValue().get(stockKey)!=null){
            return;
        }

        Map<String, Object> productMap = new HashMap<>();
        productMap.put("productPrice", product.getProductPrice());
        productMap.put("productName", product.getProductName());

        redisTemplate.opsForValue().set(stockKey,product.getQuantity(), Duration.ofDays(1));
        redisTemplate.opsForHash().putAll(productKey,productMap);
//        redisTemplate.opsForValue().set(priceKey,product.getProductPrice(),Duration.ofDays(1));
    }
//    레디스에서 재고 가져오기
    private Integer getRedisStock(Long productId){
        String key = getStockKey(productId);
        Integer stock =redisTemplate.opsForValue().get(key);
        if(stock == null){
            throw new BusinessException(ErrorCode.REDIS_NOT_FOUND);
        }
        return stock;
    }


//    제품 상태 변경
    private void changeStatus(Product product, ProductStatus status){
        Product newProduct = product.toBuilder()
                .status(status)
                .build();
        repository.save(newProduct);
    }

}