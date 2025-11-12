package com.example.projectlxp.controller.user;

import com.example.projectlxp.controller.user.dto.response.LoginResponse;
import com.example.projectlxp.controller.user.dto.request.LoginRequest;
import com.example.projectlxp.controller.user.dto.request.SignupRequest;
import com.example.projectlxp.controller.user.dto.response.UserResponse;
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

    // 로그인
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {

        return authService.login(loginRequest);
    }
}
