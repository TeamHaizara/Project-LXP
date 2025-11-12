package com.example.projectlxp.controller.uesr.dto.request;

import com.example.projectlxp.model.user.Role;
import com.example.projectlxp.model.user.User;

import java.util.List;

public class UserRequest {

    private String username;
    private String password;
    private String nickname;
    private String interest;
    private List<Role> roles;

    public UserRequest(){}

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

    public List<Role> getRoles() {
        return roles;
    }

    public User toDomain(String encodedPassword) {
        return new User(username,encodedPassword,nickname,interest,roles);
    }
}
