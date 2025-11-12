// src/main/java/com/example/projectlxp/dto/user/LoginResponse.java
package com.example.projectlxp.controller.uesr.dto.response;

import com.example.projectlxp.model.user.User;

public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;     // 토큰 만료 시간(초 단위)
    private UserResponse user;  // 사용자 정보

    public LoginResponse(String accessToken, String tokenType, long expiresIn, UserResponse user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public static LoginResponse of(String accessToken, long expiresIn, User user) {
        return new LoginResponse(accessToken, "Bearer", expiresIn / 10000000, UserResponse.from(user));
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public UserResponse getUser() {
        return user;
    }

}
