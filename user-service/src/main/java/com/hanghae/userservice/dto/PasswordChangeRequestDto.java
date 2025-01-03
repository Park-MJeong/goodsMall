package com.hanghae.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordChangeRequestDto {
    @NotBlank(message = "현재 비밀번호를 입력해주세요")
    private String currentPassword;
    @NotBlank(message = "새 비밀번호를 입력해주세요")
//    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
//            message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다")
    private String newPassword;
    @NotBlank(message = "새 비밀번호 확인을 입력해주세요")
    private String confirmPassword;
}
