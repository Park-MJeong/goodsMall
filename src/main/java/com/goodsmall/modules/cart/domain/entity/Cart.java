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

    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true) // 장바구니는 하나의 회원에만 속함
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "cart")
    private List<CartProducts> cartProducts;

}
