package com.applications.auth.dto;

import com.applications.common.exception.UnauthorizedException;

/**
 * RefreshTokenRequest - 富領域模型 DTO
 * 自我校驗權杖是否合法存在
 */
public record RefreshTokenRequest(String token) {
    public RefreshTokenRequest {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("缺少有效的 Refresh Token");
        }
    }
}
