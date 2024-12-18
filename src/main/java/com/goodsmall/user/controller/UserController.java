package com.goodsmall.user.controller;

import com.goodsmall.user.dto.AuthenticationDto;
import com.goodsmall.user.dto.UserRequestDto;
import com.goodsmall.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/authentication")
    public ResponseEntity<Void>authentication(@RequestBody UserRequestDto userRequestDto) {
        String userEmail = userRequestDto.getEmail();
        System.out.println(userEmail);
        userService.certifyEmail(userEmail);
        return ResponseEntity.ok().build();
    }

}
