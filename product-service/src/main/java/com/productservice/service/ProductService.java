package com.productservice.service;

import com.common.constant.ErrorCode;
import com.common.exception.BusinessException;
import com.common.util.SliceUtil;
import com.productservice.domain.Product;
import com.productservice.domain.ProductRepository;
import com.productservice.dto.ProductDto;
import com.productservice.dto.SliceProductDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service

@Slf4j
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }


    //전체 상품 조회
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

    public void productCheckAndUpdate(Long productId,Integer quantity){
        Product product = repository.getProduct(productId).orElseThrow(
                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        if(product.getQuantity()<quantity){
            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
        }
    }

//    제품구매시 재고감소
//    @Transactional
//    public void decreaseQuantity(OrderRequestDto dto){
//        int quantity = dto.getQuantity();
//        Product product = repository.getProduct(dto.getProductId()).orElseThrow(
//                ()->new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
//        if(product.getQuantity()<quantity){
//            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
//        }
//        log.info("감소 전 수량 {}",product.getQuantity());
//        product.setQuantity(product.getQuantity()-quantity);
//        if(product.getQuantity()==0){
//            product.setStatus("Sold Out");
//        }
//        log.info("감소 후 수량 {}",product.getQuantity());
//        repository.save(product);
//    }

//    주문취소시 재고반영
//    @Transactional
//    public void updateProductQuantities(Order order) {
//        for (OrderProducts products : order.getOrderProducts()) {
//            Product product = products.getProduct();
//            log.info("원래 수량{}", product.getQuantity());
//            product.setQuantity(product.getQuantity() + products.getQuantity());
//            if(product.getStatus().equals("Sold Out")){
//                product.setStatus("On Sale");
//            }
//            log.info(product.getStatus());
//            repository.save(product);
//            log.info("재고반영 수량{}", product.getQuantity());
//        }
//    }



}