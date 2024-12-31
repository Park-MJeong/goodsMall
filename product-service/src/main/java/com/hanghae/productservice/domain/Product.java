package com.hanghae.productservice.domain;

import com.hanghae.common.constant.ErrorCode;
import com.hanghae.common.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="product_name",nullable = false)
    private String productName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name="product_price",nullable = false)
    private BigDecimal productPrice;

    @Column(name="product_open_date")
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime openDate;

    @Column(name = "quantity")
    private int quantity;

    @Column(name="status")
    private String status;

//    제품 재고 감소
    public void decreaseQuantity(int quantity) {
        if (this.quantity < quantity) {
            throw new BusinessException(ErrorCode.QUANTITY_INSUFFICIENT);
        }
        this.quantity -= quantity;
        updateStatus();
    }
//    제품 재고 증가
    public void increaseQuantity(int quantity) {
        this.quantity += quantity;
        updateStatus();
    }

    private void updateStatus(){
        if(this.quantity==0) this.status ="Sold out";
        else if(this.quantity > 0 && this.status.equals("Sold Out")) {
            this.status = "On Sale";
        }
    }

}
