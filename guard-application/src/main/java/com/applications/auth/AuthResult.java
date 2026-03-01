package com.applications.auth;

public record AuthResult(
        LoginResponse response,
        String refreshCookie,
        String accessTokenCookie
) {
}
