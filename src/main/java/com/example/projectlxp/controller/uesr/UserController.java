package com.example.projectlxp.controller.uesr;

import com.example.projectlxp.controller.uesr.dto.request.UserRequest;
import com.example.projectlxp.controller.uesr.dto.response.UserResponse;
import com.example.projectlxp.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    // 테스트 (강사만 접근 가능)
    @GetMapping("/testIns")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public String test(){

        return "당신은 무조건 강사입니다.";
    }

    // 테스트 (강사/유저 둘다 접근 가능)
    @GetMapping("/testAll")
    @PreAuthorize("hasRole('LEARNER')")
    public String testAll(){

        return "당신은 강사 또는 학생입니다.";
    }
}