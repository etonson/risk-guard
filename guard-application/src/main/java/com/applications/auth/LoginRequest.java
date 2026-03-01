package com.applications.auth;

public record LoginRequest(
        String email,
        String username,
        String password
) {
}
