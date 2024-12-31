package com.hanghae.productservice.service;

import com.hanghae.common.constant.ErrorCode;
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
@Slf4j
public class ProductService {
    private final ProductRepository repository;

    //   공통: 제품 정보 조회
    public Product getProduct(Long id){
        return repository.getProduct(id).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_SOLD_OUT));
    }


    //   공통: 제품 수량 체크
    public Product checkStock(Long productId, int quantity) {
        Product product = getProduct(productId);
        if (product.getQuantity() < quantity) {
            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
        }
        return product;
    }

    //전체 상품 조회
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

    //    제품 상세 정보
    public ProductDto getProductDto(Long id) {
        Product product = getProduct(id);
        return new ProductDto(product);
    }


//    제품 구매시 재고감소
    @Transactional
    public void decreaseStock(Long productId,Integer quantity){
        Product product = checkStock(productId, quantity);
        product.decreaseQuantity(quantity);
        repository.save(product);
    }
    @Transactional
    public void increaseStock(Long productId,Integer quantity){
        Product product = checkStock(productId, quantity);
        product.decreaseQuantity(quantity);
        repository.save(product);
    }

////    주문취소시 재고반영
//    @Transactional
//    public void updateProductQuantities(Order order) {
//        for (OrderProducts products : order.getOrderProducts()) {
//            Product product = products.getProduct();
//            log.info("원래 수량{}", product.getQuantity());
//
//            product.increaseQuantity(product.getQuantity());
//            repository.save(product);
//
//            log.info("재고반영 수량{}", product.getQuantity());
//        }
//    }


}