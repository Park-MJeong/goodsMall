package com.goodsmall.modules.product.service;

import com.goodsmall.common.SliceUtil;
import com.goodsmall.modules.product.domain.ProductRepository;
import com.goodsmall.modules.product.dto.SliceShowProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository repository;
    //
    public Slice<SliceShowProductDto> getProductList(String search, int cursor, int size){
        int limitSize = SliceUtil.sliceSize(size);
        List<SliceShowProductDto> showProducts = repository.getProductList(search,cursor, Pageable.ofSize(limitSize));
        Slice<SliceShowProductDto> showList = SliceUtil.getSlice(showProducts,size);
        if (showList.getNumberOfElements() == 0) {
            List<SliceShowProductDto> emptyMessageList = List.of(
                    new SliceShowProductDto("더 이상 리뷰가 존재하지 않습니다.")
            );
            return SliceUtil.getSlice(emptyMessageList, size);
        }


        return showList;

    }
}