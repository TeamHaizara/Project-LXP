package com.example.projectlxp.service.user;


import com.example.projectlxp.dto.user.request.UserRequest;
import com.example.projectlxp.dto.user.response.UserResponse;
import com.example.projectlxp.model.user.User;
import com.example.projectlxp.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다 : " + id));

        // 본인만 수정 허용
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String me = auth.getName(); // 현재 로그인한 사용자의 username
        if (!me.equals(user.getUsername())) {
            System.out.println("권한x");
            return null;
        }

        if (req.getUsername() != null) user.setUsername(req.getUsername());
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        if (req.getNickname() != null) user.setNickname(req.getNickname());
        if (req.getInterest() != null) user.setInterest(req.getInterest());
        if (req.getRoles() != null && !req.getRoles().isEmpty()) {
            user.setRoles(req.getRoles()); // 타입/매핑에 맞게 세터 구현
        }

        // JPA 변경감지로 flush
        return UserResponse.from(user);
    }

    // 삭제
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}


