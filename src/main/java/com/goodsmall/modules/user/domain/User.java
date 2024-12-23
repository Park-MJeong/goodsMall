package com.goodsmall.modules.user.domain;

import com.goodsmall.modules.cart.domain.entity.Cart;
import com.goodsmall.modules.user.dto.UserRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="user_name",nullable = false)
    private String userName;

    @Column(name="phone_number",nullable = false)
    private String phoneNumber;

    @Column(name="address",nullable = false)
    private String address;

    @Column(name="email",nullable = false,unique = true)
    private String email;

    @Column(name="password",nullable = false)
    private String password;

    @Column
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Cart cart;

    public User(UserRequestDto dto) {
        this.userName = dto.getUserName();
        this.phoneNumber = dto.getPhoneNumber();
        this.address = dto.getAddress();
        this.email = dto.getEmail();
        this.password = dto.getPassword();
        this.role = UserRoleEnum.USER;
    }

    public enum UserRoleEnum {
        USER, ADMIN
    }
}
