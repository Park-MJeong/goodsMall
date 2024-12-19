package com.goodsmall.modules.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Entity
@Getter
@NoArgsConstructor
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

    @Builder
    public User(String userName, String phoneNumber, String address, String email, String password) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.password = password;
        this.role = UserRoleEnum.USER;
    }

    public enum UserRoleEnum {
        USER, ADMIN
    }
}
