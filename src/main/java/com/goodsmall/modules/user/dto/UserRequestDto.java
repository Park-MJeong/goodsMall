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

    public UserRequestDto() {}
    public UserRequestDto(String userName, String phoneNumber, String address,
                          String email, String password) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.password = password;
    }

}
