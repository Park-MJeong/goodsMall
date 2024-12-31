package com.hanghae.userservice.jwt.dto;

import lombok.Getter;

@Getter
public class LoginUserRequestDto {
    private String email;
    private String password;

}

