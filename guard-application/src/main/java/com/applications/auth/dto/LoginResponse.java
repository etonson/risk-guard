package com.applications.auth.dto;

import java.util.List;

public record LoginResponse(
        UserInfo user,
        String accessToken
) {
    public record UserInfo(
            Long id,
            String email,
            String name,
            List<String> roles
    ) {}

    public static LoginResponse from(com.domain.user.User user, String accessToken) {
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

