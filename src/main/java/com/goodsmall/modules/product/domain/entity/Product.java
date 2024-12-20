package com.goodsmall.modules.product.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

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

    @Column(name = "image", nullable = false)
    private String image;


}
