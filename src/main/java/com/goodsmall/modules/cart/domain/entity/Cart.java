package com.goodsmall.modules.cart.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.goodsmall.modules.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "cart")
    private List<CartProducts> cartProducts;

    public Cart() {}

    public Cart(Long userId) {
        this.userId = userId;
    }

}
