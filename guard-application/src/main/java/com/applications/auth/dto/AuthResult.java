package com.applications.auth.dto;

import lombok.Getter;

/**
 * AuthResult - 封裝認證結果與 Cookie 資料
 */
@Getter
public class AuthResult {

    private final LoginResponse response;
    private final String refreshCookie;
    private final String accessTokenCookie;

    public AuthResult(LoginResponse response, String refreshCookie, String accessTokenCookie) {
        this.response = response;
        this.refreshCookie = refreshCookie;
        this.accessTokenCookie = accessTokenCookie;
    }

    /**
     * 獲取前端所需的回應主體
     */
    public LoginResponse response() {
        return response;
    }
}
