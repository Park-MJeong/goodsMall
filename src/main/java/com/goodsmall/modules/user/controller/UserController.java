package com.goodsmall.modules.user.controller;

import com.goodsmall.common.api.ApiResponse;
import com.goodsmall.common.security.jwt.CustomUserDetails;
import com.goodsmall.modules.user.dto.*;
import com.goodsmall.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/password")
    public ResponseEntity<ApiResponse<?>>changePassword(@Valid@RequestBody PasswordChangeRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails){
        ApiResponse<?> response =  userService.changePassword(userDetails.getId(),requestDto);;
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse<?>>login(@Valid @RequestBody LoginUserRequestDto loginUserRequestDto, HttpServletResponse servletResponse) {
//        ApiResponse<?> response = userService.login(loginUserRequestDto,servletResponse);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<ApiResponse<?>>logout(@RequestHeader("Authorization") String accessToken, @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Long userId = userDetails.getId();
//        ApiResponse<?> response = userService.logout(accessToken,userId);
//        return ResponseEntity.ok(response);
//    }

}
