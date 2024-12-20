package com.goodsmall.common;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public class SliceUtil {
    public static <T> org.springframework.data.domain.Slice<T> getSlice(List<T> list, int size) {

        boolean hasNext =false;
        if (list.size() > size) {
            list.remove(size);
            hasNext = true;
        }
        return new SliceImpl<>(list, Pageable.ofSize(size),hasNext); //Pageable.ofSize(size) 반환할 데이터 크기
    }
    public static int sliceSize(int size){ //다음페이지가 있는지 확인하기 위해
        return size+1;
    }
}
