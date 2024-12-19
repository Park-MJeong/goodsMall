package com.goodsmall.modules.user.controller;

import com.goodsmall.modules.user.dto.AuthenticationDto;
import com.goodsmall.modules.user.dto.UserRequestDto;
import com.goodsmall.modules.user.service.UserService;
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

    @PostMapping("/authentication")
    public ResponseEntity<Void>authentication(@RequestBody AuthenticationDto dto) {
        String userEmail = dto.getEmail();
        log.info("컨트롤러:: userEmail: " + userEmail);
        userService.certifyEmail(userEmail);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/signup")
    public ResponseEntity<?>signup(@RequestBody UserRequestDto userRequestDto) {
        log.info(userRequestDto.getUserName());
        userService.signup(userRequestDto);
        return ResponseEntity.ok("회원가입 완료");
    }

}
