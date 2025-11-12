package com.example.projectlxp.controller.uesr.dto.response;

import com.example.projectlxp.model.user.Role;
import com.example.projectlxp.model.user.User;

import java.util.List;

public class UserResponse {

    public Long userId;
    public String username;
    public String nickname;
    public String interest;
    public List<Role> roles;

    public UserResponse(
            Long userId, String username, String nickname, String interest, List<Role> roles) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.interest = interest;
        this.roles = roles;
    }

    public static UserResponse from(User user) {
        return new UserResponse(
            user.getUserId(),
            user.getUsername(),
            user.getNickname(),
            user.getInterest(),
            user.getRoles()
        );
    }
}
