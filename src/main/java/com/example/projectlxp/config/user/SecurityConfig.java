package com.example.projectlxp.config.user;

import com.example.projectlxp.jwt.JwtFilter;
import com.example.projectlxp.jwt.JwtUtil;
import com.example.projectlxp.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
        throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // csrf 비활성화
        http
            .csrf((auth) -> auth.disable());

        // form로그인 방식 비활성화
        http
            .formLogin((auth) -> auth.disable());

        // http basic 인증 방식 비활성화
        http
            .httpBasic((auth) -> auth.disable());

        // 역할벌 인가
        http
            .authorizeHttpRequests((auth)
                -> auth
                .requestMatchers("/api/auth/login", "/", "/api/auth/join").permitAll()
                .requestMatchers("/api/instructor/**").hasRole("INSTRUCTOR")
                .requestMatchers("/api/learner/**").hasRole("LEARNER")
                .anyRequest().authenticated());

        // JwtFilter 등록
        http
            .addFilterBefore(new JwtFilter(jwtUtil, userDetailsService), LoginFilter.class);

        // 세션 설정
        http
            .sessionManagement((session)
                -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
