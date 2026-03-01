package com.applications.auth;

public record RegisterRequest(
        String email,
        String username,
        String password
) {}
