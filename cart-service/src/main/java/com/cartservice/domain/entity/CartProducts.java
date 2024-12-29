package com.cartservice.domain.entity;


import com.productservice.domain.Product;
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

    private long productId;

    private long cartId;
    public CartProducts() {}

    public CartProducts(long cartId,long productId, int quantity) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
