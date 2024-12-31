package com.hanghae.userservice.domain;

import com.hanghae.userservice.dto.UserRequestDto;
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

    public void changePassword(String newPassword) {
        this.password = newPassword;
//        this.updatedAt = LocalDateTime.now();
    }
}
