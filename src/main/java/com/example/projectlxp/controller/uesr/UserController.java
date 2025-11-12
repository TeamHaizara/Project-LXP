package com.example.projectlxp.controller.uesr;

import com.example.projectlxp.dto.user.request.SignupRequest;
import com.example.projectlxp.dto.user.request.UserRequest;
import com.example.projectlxp.dto.user.response.UserResponse;
import com.example.projectlxp.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    // 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {

        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}