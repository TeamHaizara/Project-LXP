package com.example.projectlxp.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. 헤더 찾고 검증
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {

            System.out.println("토큰이 존재하지 않습니다.");
            filterChain.doFilter(request, response);

            return;
        }

        String token = authorizationHeader.substring(7);

        // 2. 토큰 소멸 시간 검증(토큰이 유효하지 않으면 더 진행 X)
        if (jwtUtil.isExpired(token)) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":\"TOKEN_EXPIRED\",\"message\":\"토큰이 만료되었습니다.\"}");

            return;
        }

        // 3. 토큰에서 사용자/역할 추출
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        if (!role.startsWith("ROLE_"))
            role = "ROLE_" + role;

        // 4. 강사 > 학생 권한 포함
        Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority(role));

        if ("ROLE_INSTRUCTOR".equals(role)) {

            authorities.add(new SimpleGrantedAuthority("ROLE_LEARNER"));
        }

        // 5. 이미 인증된 상태가 아니면 컨텍스트에 심기
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            authToken.setDetails(new org
                    .springframework
                    .security
                    .web
                    .authentication
                    .WebAuthenticationDetailsSource()
                    .buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 6. 반드시 한 번만 호출
        filterChain.doFilter(request, response);
    }
}
