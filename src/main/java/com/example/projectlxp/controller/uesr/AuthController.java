package com.example.projectlxp.controller.uesr;

import com.example.projectlxp.dto.user.LoginResponse;
import com.example.projectlxp.dto.user.request.LoginRequest;
import com.example.projectlxp.dto.user.request.SignupRequest;
import com.example.projectlxp.dto.user.response.UserResponse;
import com.example.projectlxp.service.user.AuthService;
import com.example.projectlxp.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {

        this.authService = authService;
    }

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<UserResponse> join(@RequestBody SignupRequest signupRequest) {

        UserResponse createdUser = authService.createUser(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
