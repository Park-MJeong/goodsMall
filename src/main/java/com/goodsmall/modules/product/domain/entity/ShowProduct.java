package com.goodsmall.modules.product.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="show_products")
public class ShowProduct { //현재 판매중인 상품들
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="product_open_date")
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private LocalDateTime openDate;

    @Column(name="product_price",nullable = false)
    private long productPrice;

    @Column(name = "quantity")
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


}
