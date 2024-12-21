package com.goodsmall.modules.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SliceProductDto {
    private long id;
    private long productId;
    private String productName;
    private Long productPrice;
    private String image;
    private LocalDateTime openDate;
    private String status;
    private String message; // 메시지 필드 추가

    // slice의 한 객체에 담기는 정보
    public SliceProductDto(long id, String productName, Long price, String image, LocalDateTime openDate,String status) {
        this.id = id;
        this.productName = productName;
        this.productPrice = price;
        this.image = image;
        this.openDate = openDate;
        this.status = status;
    }

    public SliceProductDto(String message) {
        this.message = message;
    }
}
