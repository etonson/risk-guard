package com.applications.auth.dto;

public record AuthResult(
        LoginResponse response,
        String refreshCookie,
        String accessTokenCookie
) {
}
