package com.controllers.auth;

import com.applications.auth.AuthApplicationService;
import com.applications.auth.dto.*;
import com.applications.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Authentication Controller - 生產級優化版本
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthApplicationService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest req) {
        AuthResult result = authService.login(req);
        return createAuthResponse(result, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest req) {
        AuthResult result = authService.register(req);
        // 語義化：註冊成功返回 201 Created
        return createAuthResponse(result, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        List<String> cookies = authService.logout();
        var builder = ResponseEntity.ok();
        cookies.forEach(cookie -> builder.header(HttpHeaders.SET_COOKIE, cookie));
        return builder.body(ApiResponse.success(null, "登出成功"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @RequestBody(required = false) RefreshTokenRequest request,
            @org.springframework.web.bind.annotation.CookieValue(value = "refresh_token", required = false) String cookieToken
    ) {
        String token = (request != null && request.token() != null) ? request.token() : cookieToken;
        
        if (token == null || token.isBlank()) {
            throw new com.applications.common.exception.UnauthorizedException("Refresh Token 已遺失或過期");
        }
        
        AuthResult result = authService.refresh(token);
        return createAuthResponse(result, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfo>> me() {
        // 呼叫 Application Service 獲取目前登入者資訊
        UserInfo userInfo = authService.me();
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    /**
     * 輔助方法：統一封裝認證相關的回應標頭與狀態碼
     */
    private ResponseEntity<ApiResponse<LoginResponse>> createAuthResponse(AuthResult result, HttpStatus status) {
        var builder = ResponseEntity.status(status);
        
        if (result.getRefreshCookie() != null) {
            builder.header(HttpHeaders.SET_COOKIE, result.getRefreshCookie());
        }
        if (result.getAccessTokenCookie() != null) {
            builder.header(HttpHeaders.SET_COOKIE, result.getAccessTokenCookie());
        }
        
        return builder.body(ApiResponse.success(result.getResponse()));
    }
}
