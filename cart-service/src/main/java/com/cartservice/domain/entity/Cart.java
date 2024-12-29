package com.cartservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.userservice.domain.User;
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
    private long userId;

//    @OneToMany(mappedBy = "cart")
//    private List<CartProducts> cartProducts;

}
