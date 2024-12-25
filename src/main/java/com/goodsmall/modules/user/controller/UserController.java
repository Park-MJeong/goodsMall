package com.goodsmall.modules.user.controller;

import com.goodsmall.common.api.ApiResponse;
import com.goodsmall.modules.user.service.UserService;
import com.goodsmall.modules.user.dto.EmailRequestDto;
import com.goodsmall.modules.user.dto.UserRequestDto;
import com.goodsmall.modules.user.dto.VerifyDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/email-send")
    public ResponseEntity<ApiResponse<?>> sendEmail(@Valid @RequestBody EmailRequestDto dto) {
        ApiResponse<?> response = userService.sendEmail(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email-verify")
    public ResponseEntity<ApiResponse<?>>verify(@Valid @RequestBody VerifyDto dto) {
        ApiResponse<?> response = userService.verifyEmail(dto);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>>signup(@Valid @RequestBody UserRequestDto userRequestDto) {
        ApiResponse<?> response = userService.signup(userRequestDto);
        return ResponseEntity.ok(response);
    }

}
