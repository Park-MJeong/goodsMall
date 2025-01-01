package com.hanghae.cartservice.domain.entity;

import com.hanghae.cartservice.dto.CartProductInfo;
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

    @Column(name = "product_id")
    private long productId;

    @ManyToOne
    @JoinColumn(name="cart_id")
    private Cart cart;

    public CartProducts() {}

    public CartProducts(Cart cart, CartProductInfo dto) {
        this.cart = cart;
        this.productId = dto.getProductId();
        this.quantity =dto.getQuantity();
    }
}
