package com.goodsmall.modules.cart.domain.entity;

import com.goodsmall.modules.product.domain.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cart_products")
public class CartProducts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="quantity",nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name="cart_id")
    private Cart cart;
}
