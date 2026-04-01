package com.applications.auth.dto;

import com.domain.user.User;

import java.util.List;

public record LoginResponse(
        UserInfo user,
        String accessToken
) {
    public static LoginResponse from(User user, String accessToken) {
        return new LoginResponse(
                new UserInfo(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername(), // Use username as name
                        List.of("USER")
                ),
                accessToken
        );
    }
}