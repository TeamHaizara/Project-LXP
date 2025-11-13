package com.example.projectlxp.service.user;


import com.example.projectlxp.controller.user.dto.request.UserRequest;
import com.example.projectlxp.controller.user.dto.response.UserResponse;
import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.user.User;
import com.example.projectlxp.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 전체조회
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    // 단일 조회
    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        User user = userRepository.findByUserId(id)
                .orElseThrow(() -> BusinessException
                        .builder(ExceptionCode.USER_NOT_FOUND_BY_ID)
                        .withId(id)
                        .build());

        return UserResponse.from(user);
    }

    // 내 정보 수정
    @Transactional
    public UserResponse updateUser(Long id, UserRequest req) {
        User user = userRepository.findByUserId(id)
                .orElseThrow(() -> BusinessException
                        .builder(ExceptionCode.USER_NOT_FOUND_BY_ID)
                        .withId(id)
                        .build());

        updateUserFields(req, user);

        return UserResponse.from(user);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(Long id) {
        userRepository.findByUserId(id)
                .ifPresentOrElse(User::softDelete, ()-> {
            throw BusinessException
                    .builder(ExceptionCode.USER_NOT_FOUND_BY_ID)
                    .withId(id)
                    .build();
        });
    }

    private void updateUserFields(UserRequest req, User user) {
        if (req.getUsername() != null) user.setUsername(req.getUsername());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        if (req.getNickname() != null) user.setNickname(req.getNickname());
        if (req.getInterest() != null) user.setInterest(req.getInterest());
        if (req.getRoles() != null && !req.getRoles().isEmpty()) {
            user.setRoles(req.getRoles());
        }
    }
}


