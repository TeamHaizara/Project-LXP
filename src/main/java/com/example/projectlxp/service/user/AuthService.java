package com.example.projectlxp.service.user;

import com.example.projectlxp.dto.user.LoginResponse;
import com.example.projectlxp.dto.user.request.LoginRequest;
import com.example.projectlxp.dto.user.request.SignupRequest;
import com.example.projectlxp.dto.user.response.UserResponse;
import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.jwt.JwtUtil;
import com.example.projectlxp.model.user.User;
import com.example.projectlxp.repository.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private static final long ACCESS_EXPIRE_MS = 600*600*100L;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    // 회원가입
    public UserResponse createUser(SignupRequest signupRequest) {

        if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        User user = signupRequest.toDomain(encodedPassword);

        User saved = userRepository.save(user);

        return UserResponse.from(saved);
    }

    // 로그인
    public LoginResponse login(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 통과시킨 인증 정보에서 사용자명/권한 꺼내기
        String principalName = authentication.getName();

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .orElse("ROLE_LEARNER");

        // 토큰 발급 (JwtUtil 사용)
        String accessToken = jwtUtil.createToken(principalName, role, ACCESS_EXPIRE_MS);

        User user = userRepository.findByUsername(principalName)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디의 유저를 찾지 못했습니다."));

        return LoginResponse.of(accessToken, ACCESS_EXPIRE_MS, user);
    }
}
