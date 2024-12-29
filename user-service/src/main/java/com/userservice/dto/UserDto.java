package com.userservice.dto;

import com.userservice.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String username;
    private String password;
    private String address;
    private String email;
    private User.UserRoleEnum auth;

    @Builder(toBuilder = true)
    private UserDto(String username, String password,String address, String email, User.UserRoleEnum auth) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.email = email;
        this.auth = auth;
    }
}
