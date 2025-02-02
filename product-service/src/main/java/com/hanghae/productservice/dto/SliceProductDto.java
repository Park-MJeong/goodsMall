package com.hanghae.productservice.dto;

import com.hanghae.productservice.domain.Product;
import com.hanghae.productservice.domain.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SliceProductDto {
    private long id;
    private String productName;
    private BigDecimal productPrice;
    private LocalDateTime openDate;
    private ProductStatus status;
    private String message; // 메시지 필드 추가

    // slice의 한 객체에 담기는 정보
    public SliceProductDto(long id, String productName, BigDecimal price, LocalDateTime openDate, ProductStatus status) {
        this.id = id;
        this.productName = productName;
        this.productPrice = price;
        this.openDate = openDate;
        this.status = status;
    }

    public SliceProductDto(String message) {
        this.message = message;
    }

    public static SliceProductDto fromProductDto(Product product) {
        return new SliceProductDto(
                product.getId(),
                product.getProductName(),
                product.getProductPrice(),
                product.getOpenDate(),
                product.getStatus()
        );
    }
}
