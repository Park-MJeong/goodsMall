package com.goodsmall.modules.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SliceShowProductDto {
    private long id;
    private long productId;
    private String productName;
    private Long price;
    private String imageUrl;
    private LocalDateTime openDate;
    private String message; // 메시지 필드 추가

    // slice의 한 객체에 담기는 정보
    public SliceShowProductDto(long id,long productId, String productName, Long price, String imageUrl, LocalDateTime openDate) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.openDate = openDate;
    }

    public SliceShowProductDto(String message) {
        this.message = message;
    }
}
