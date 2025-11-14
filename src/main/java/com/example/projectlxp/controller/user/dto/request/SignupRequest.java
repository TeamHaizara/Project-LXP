package com.example.projectlxp.controller.user.dto.request;

import com.example.projectlxp.model.user.Role;
import com.example.projectlxp.model.user.User;

import java.util.ArrayList;
import java.util.List;

public class SignupRequest {

    private String username;
    private String password;
    private String nickname;
    private String interest;

    public SignupRequest(){}

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getInterest() {
        return interest;
    }

    public User toDomain(String encodedPassword) {
        return new User(username,encodedPassword,nickname,interest,List.of(Role.ROLE_LEARNER));
    }
}
