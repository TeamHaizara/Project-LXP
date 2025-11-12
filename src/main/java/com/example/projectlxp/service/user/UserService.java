package com.example.projectlxp.service.user;


import com.example.projectlxp.controller.user.dto.request.UserRequest;
import com.example.projectlxp.controller.user.dto.response.UserResponse;
import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.user.User;
import com.example.projectlxp.repository.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    // 단일 조회
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자가 없습니다."));
        return UserResponse.from(user);
    }

    // 수정
    @Transactional
    public UserResponse updateUser(Long id, UserRequest req) {
        User user = userRepository.findByUserIdAndActiveIsTrue(id)
                .orElseThrow(() -> BusinessException.builder(ExceptionCode.USER_NOT_FOUND_BY_ID).withId(id).build());

        validateUser(user);
        validateUpdateValue(req, user);

        return UserResponse.from(user);
    }

    // 삭제
    public void deleteUser(Long id) {
        userRepository.findByUserIdAndActiveIsTrue(id).ifPresentOrElse(User::softDelete,()->{
            throw BusinessException
                    .builder(ExceptionCode.USER_NOT_FOUND_BY_ID)
                    .withId(id)
                    .build();
        });
    }

    private void validateUser(User user) {
        // 본인만 수정 허용
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String me = auth.getName(); // 현재 로그인한 사용자의 username
        if (!me.equals(user.getUsername())) {
            throw BusinessException.builder(ExceptionCode.USER_NOT_FOUND_BY_NAME).withField(user.getUsername()).build();
        }
    }

    private void validateUpdateValue(UserRequest req, User user) {
        if (req.getUsername() != null) user.setUsername(req.getUsername());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        if (req.getNickname() != null) user.setNickname(req.getNickname());
        if (req.getInterest() != null) user.setInterest(req.getInterest());
        if (req.getRoles() != null && !req.getRoles().isEmpty()) {
            user.setRoles(req.getRoles()); // 타입/매핑에 맞게 세터 구현
        }
    }
}


