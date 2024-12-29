package com.goodsmall.modules.product.service;

import com.goodsmall.common.constant.ErrorCode;
import com.goodsmall.common.exception.BusinessException;
import com.goodsmall.common.util.SliceUtil;
import com.goodsmall.modules.order.domain.entity.Order;
import com.goodsmall.modules.order.domain.entity.OrderProducts;
import com.goodsmall.modules.order.dto.OrderRequestDto;
import com.goodsmall.modules.product.domain.Product;
import com.goodsmall.modules.product.domain.ProductRepository;
import com.goodsmall.modules.product.dto.ProductDto;
import com.goodsmall.modules.product.dto.SliceProductDto;
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

    //전체 상품 조회
    public Slice<SliceProductDto> getProductList(String search, Long cursor, Integer size){
        int limitSize = SliceUtil.sliceSize(size);
        List<Product> products = repository.getProductList(search,cursor, Pageable.ofSize(limitSize));
        List<SliceProductDto> productDtos = products.stream().map(product ->
                        new SliceProductDto(product.getId(), product.getProductName(),product.getProductPrice(), product.getOpenDate(),product.getStatus()))
                .collect(Collectors.toList());
        Slice<SliceProductDto> showList = SliceUtil.getSlice(productDtos,size);
        if (showList.getNumberOfElements() == 0) {
            List<SliceProductDto> emptyMessageList = List.of(
                    new SliceProductDto("더 이상 상품이 존재하지 않습니다.")
            );
            return SliceUtil.getSlice(emptyMessageList, size);
        }

        return showList;
    }

    public Product getProduct(Long id){
        return repository.getProduct(id).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_SOLD_OUT));

    }
    public ProductDto getProductDto(Long id) {
        Product product = getProduct(id);
        return new ProductDto(product);
    }

//    public void productCheckAndUpdate(CreateOrderRequestDto dto){
//        Product product = repository.getProduct(dto.getProductId()).orElseThrow(
//                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
//        if(product.getQuantity()<dto.getQuantity()){
//            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
//        }
//    }

//    제품구매시 재고감소
    @Transactional
    public void decreaseQuantity(Integer quantity, Long productId){
        Product product = getProduct(productId);
        if(product.getQuantity()<quantity){
            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
        }
        log.info("감소 전 수량 {}",product.getQuantity());
        product.setQuantity(product.getQuantity()-quantity);
        if(product.getQuantity()==0){
            product.setStatus("Sold Out");
        }
        log.info("감소 후 수량 {}",product.getQuantity());
        repository.save(product);
    }

//    주문취소시 재고반영
    @Transactional
    public void updateProductQuantities(Order order) {
        for (OrderProducts products : order.getOrderProducts()) {
            Product product = products.getProduct();
            log.info("원래 수량{}", product.getQuantity());
            product.setQuantity(product.getQuantity() + products.getQuantity());
            if(product.getStatus().equals("Sold Out")){
                product.setStatus("On Sale");
            }
            log.info(product.getStatus());
            repository.save(product);
            log.info("재고반영 수량{}", product.getQuantity());
        }
    }



}