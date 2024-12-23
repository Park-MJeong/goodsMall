package com.goodsmall.modules.product.service;

import com.goodsmall.common.constant.ErrorCode;
import com.goodsmall.common.exception.BusinessException;
import com.goodsmall.common.util.SliceUtil;
import com.goodsmall.modules.order.dto.CreateOrderRequestDto;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository repository;
    //
    public Slice<SliceProductDto> getProductList(String search, Long cursor, Integer size){
        int limitSize = SliceUtil.sliceSize(size);
        List<SliceProductDto> showProducts = repository.getProductList(search,cursor, Pageable.ofSize(limitSize));
        Slice<SliceProductDto> showList = SliceUtil.getSlice(showProducts,size);
        if (showList.getNumberOfElements() == 0) {
            List<SliceProductDto> emptyMessageList = List.of(
                    new SliceProductDto("더 이상 상품이 존재하지 않습니다.")
            );
            return SliceUtil.getSlice(emptyMessageList, size);
        }

        return showList;
    }

    public ProductDto getProduct(Long id){
        return repository.getProductInformation(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

    }

//    public void productCheckAndUpdate(CreateOrderRequestDto dto){
//        Product product = repository.getProduct(dto.getProductId()).orElseThrow(
//                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
//        if(product.getQuantity()<dto.getQuantity()){
//            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
//        }
//    }

    @Transactional
    public void decreaseQuantity(CreateOrderRequestDto dto){
        int quantity = dto.getQuantity();
        Product product = repository.getProduct(dto.getProductId()).orElseThrow(
                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        if(product.getQuantity()<quantity){
            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
        }
        log.info("감소 전 수량 {}",product.getQuantity());
        product.setQuantity(quantity);
        if(product.getQuantity()==0){
            product.setStatus("Sold Out");
        }
        log.info("감소 후 수량 {}",product.getQuantity());
        repository.save(product);
    }


}