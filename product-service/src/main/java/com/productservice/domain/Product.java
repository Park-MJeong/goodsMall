package com.productservice.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
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

    @Setter
    @Column(name="status")
    private String status;


//    public void setStatus(String status) {
//        this.status = status;
//    }


}
