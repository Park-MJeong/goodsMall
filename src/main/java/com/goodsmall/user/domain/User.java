package com.goodsmall.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
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


}
