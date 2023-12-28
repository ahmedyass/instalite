package com.instalite.instalite.controller;

import com.instalite.instalite.dto.LoginDto;
import com.instalite.instalite.dto.RegisterDto;
import com.instalite.instalite.dto.RegisterResponseDto;
import com.instalite.instalite.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody RegisterDto request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<RegisterResponseDto> login(@RequestBody LoginDto request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
