package com.applications.auth.dto;

public record LoginRequest(
        String email,
        String username,
        String password
) {
}
