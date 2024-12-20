package com.goodsmall.modules.user.dto;

import com.goodsmall.common.security.EncryptionUtil.EncryptionService;
import com.goodsmall.modules.user.domain.User;
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
    private String verifyCode;

    public UserRequestDto(String userName, String phoneNumber, String address,
                          String email, String password) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.password = password;
    }

}
