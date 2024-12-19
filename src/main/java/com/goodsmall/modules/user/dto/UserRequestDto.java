package com.goodsmall.modules.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    private String userName;
    private String phoneNumber;
    private String address;
    private String email;
    private String password;
    private String certifyCode;
}
