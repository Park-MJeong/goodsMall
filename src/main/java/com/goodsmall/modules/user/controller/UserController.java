package com.goodsmall.modules.user.controller;

import com.goodsmall.common.redis.RedisService;
import com.goodsmall.modules.user.dto.EmailRequestDto;
import com.goodsmall.modules.user.dto.VerifyDto;
import com.goodsmall.modules.user.dto.UserRequestDto;
import com.goodsmall.modules.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final RedisService redisService;

    public UserController(UserService userService, RedisService redisService) {
        this.userService = userService;
        this.redisService = redisService;
    }

    @PostMapping("/email-send")
    public ResponseEntity<?>mailSend(@Valid @RequestBody EmailRequestDto dto) {
        userService.verifyEmail(dto);
        return ResponseEntity.ok("이메일 전송이 완료되었습니다.");
    }

    @PostMapping("/email-verify")
    public ResponseEntity<?>verify(@Valid @RequestBody VerifyDto dto) {
        redisService.checkData(dto);
        return ResponseEntity.ok("이메일 인증에 성공하였습니다.");
    }
    @PostMapping("/signup")
    public ResponseEntity<?>signup(@Valid @RequestBody UserRequestDto userRequestDto) {
        userService.signup(userRequestDto);
        return ResponseEntity.ok("회원가입 완료");
    }

}
