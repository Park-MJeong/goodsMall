package com.hanghae.productservice.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hanghae.common.exception.ErrorCode;
import com.hanghae.common.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@Table(name = "products")
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="product_name",nullable = false)
    private String productName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name="product_price",nullable = false)
    private BigDecimal productPrice;

    @Column(name="product_open_date")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime openDate;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name="status")
    private String status;

    public Product(long id, String productName, String description, BigDecimal productPrice, LocalDateTime openDate, int quantity, String status) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.productPrice = productPrice;
        this.openDate = openDate;
        this.quantity = quantity;
        this.status = status;

    }

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

    public void statusOnSale(Product product){
        this.status = "On Sale";
    }

}
