package com.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyDto {
    @NotBlank
    private String email;
    @NotBlank
    private String verifyCode;

}
