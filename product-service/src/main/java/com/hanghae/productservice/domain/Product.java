package com.hanghae.productservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime openDate;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    public Product(Long id, String productName, String description, BigDecimal productPrice, LocalDateTime openDate, int quantity, ProductStatus status) {
        this.id = id;
        this.productName = productName;
        this.description = description;
        this.productPrice = productPrice;
        this.openDate = openDate;
        this.quantity = quantity;
        this.status = status;

    }


////    제품 재고 증가
//    public void increaseQuantity(int quantity) {
//        this.quantity += quantity;
//        updateStatus();
//    }




}
