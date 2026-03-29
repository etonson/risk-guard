package com.applications.auth.dto;

import com.applications.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

/**
 * AuthResult - 封裝認證結果與 Cookie 設定邏輯
 */
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
     * 將 Cookie 寫入回應標頭，並直接返回 API 回應物件
     */
    public ApiResponse<LoginResponse> writeTo(HttpServletResponse response) {
        addCookieIfPresent(response, refreshCookie);
        addCookieIfPresent(response, accessTokenCookie);
        return ApiResponse.success(this.response);
    }

    private void addCookieIfPresent(HttpServletResponse response, String cookie) {
        if (cookie != null) {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie);
        }
    }

    /**
     * 獲取前端所需的回應主體
     */
    public LoginResponse response() {
        return response;
    }
}
