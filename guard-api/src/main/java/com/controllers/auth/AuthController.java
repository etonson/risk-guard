package com.controllers.auth;

import com.applications.auth.AuthApplicationService;
import com.applications.auth.dto.*;
import com.applications.common.dto.ApiResponse;
import com.applications.common.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Authentication Controller - 生產級優化版本
 *
 *
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "使用者登入、註冊、Token 刷新與登出")
public class AuthController {

    private final AuthApplicationService authService;

    @PostMapping("/login")
    @Operation(summary = "使用者登入", description = "驗證使用者憑據，成功後核發 JWT。支援以 email 或 username 登入。")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest req) {
        AuthResult result = authService.login(req);
        return createAuthResponse(result, HttpStatus.OK);
    }

    @PostMapping("/register")
    @Operation(summary = "使用者註冊", description = "建立新使用者帳號。")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest req) {
        AuthResult result = authService.register(req);
        // 語義化：註冊成功返回 201 Created
        return createAuthResponse(result, HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    @Operation(summary = "使用者登出", description = "清除客戶端的 HttpOnly Cookies。")
    public ResponseEntity<ApiResponse<Void>> logout() {
        List<String> cookies = authService.logout();
        var builder = ResponseEntity.ok();
        cookies.forEach(cookie -> builder.header(HttpHeaders.SET_COOKIE, cookie));
        return builder.body(ApiResponse.success(null, "登出成功"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "刷新 Access Token", description = "支援從 Request Body 或 Cookie 中的 refresh_token 讀取，並核發新的 access_token。")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            @RequestBody(required = false) RefreshTokenRequest request,
            @CookieValue(value = "refresh_token", required = false) String cookieToken
    ) {
        String token = (request != null && request.token() != null) ? request.token() : cookieToken;
        
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Refresh Token 已遺失或過期");
        }
        
        AuthResult result = authService.refresh(token);
        return createAuthResponse(result, HttpStatus.OK);
    }

    @GetMapping("/me")
    @Operation(summary = "獲取當前用戶資訊", description = "返回當前已登入使用者的基本資料與角色。")
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
