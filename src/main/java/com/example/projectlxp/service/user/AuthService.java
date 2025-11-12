package com.example.projectlxp.service.user;

import com.example.projectlxp.controller.user.dto.response.LoginResponse;
import com.example.projectlxp.controller.user.dto.request.LoginRequest;
import com.example.projectlxp.controller.user.dto.request.SignupRequest;
import com.example.projectlxp.controller.user.dto.response.UserResponse;
import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
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

        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw BusinessException
                    .builder(ExceptionCode.USER_ALREADY_EXISTS)
                    .withField(signupRequest.getUsername())
                    .build();
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
                .orElseThrow(() -> BusinessException
                        .builder(ExceptionCode.USER_NOT_FOUND_BY_NAME)
                        .withField(principalName)
                        .build());

        return LoginResponse.of(accessToken, ACCESS_EXPIRE_MS, user);
    }
}
