package com.controllers.auth;

import com.applications.auth.AuthApplicationService;
import com.applications.auth.dto.AuthResult;
import com.applications.auth.dto.LoginRequest;
import com.applications.auth.dto.LoginResponse;
import com.applications.auth.dto.RegisterRequest;
import com.applications.common.dto.ApiResponse;
import com.applications.common.dto.ResultCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Authentication Controller
 *
 * @Author: Eton.Lin
 * @Date: 2026/1/4 下午9:01
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthApplicationService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @RequestBody LoginRequest req,
            HttpServletResponse response
    ) {
        AuthResult result = authService.login(req);

        if (result.refreshCookie() != null) {
            response.addHeader(HttpHeaders.SET_COOKIE, result.refreshCookie());
        }
        if (result.accessTokenCookie() != null) {
            response.addHeader(HttpHeaders.SET_COOKIE, result.accessTokenCookie());
        }

        return ApiResponse.success(result.response());
    }

    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(
            @RequestBody RegisterRequest req,
            HttpServletResponse response
    ) {
        AuthResult result = authService.register(req);

        if (result.refreshCookie() != null) {
            response.addHeader(HttpHeaders.SET_COOKIE, result.refreshCookie());
        }
        if (result.accessTokenCookie() != null) {
            response.addHeader(HttpHeaders.SET_COOKIE, result.accessTokenCookie());
        }

        return ApiResponse.success(result.response());
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse response) {
        List<String> cookies = authService.logout();
        for (String cookie : cookies) {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie);
        }
        return ApiResponse.success(null, "登出成功");
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null) {
            return ApiResponse.error(ResultCode.UNAUTHORIZED, "缺少 Refresh Token");
        }

        try {
            AuthResult result = authService.refresh(refreshToken);

            if (result.refreshCookie() != null) {
                response.addHeader(HttpHeaders.SET_COOKIE, result.refreshCookie());
            }
            if (result.accessTokenCookie() != null) {
                response.addHeader(HttpHeaders.SET_COOKIE, result.accessTokenCookie());
            }

            return ApiResponse.success(result.response());
        } catch (Exception e) {
            return ApiResponse.error(ResultCode.UNAUTHORIZED, "無效的 Refresh Token");
        }
    }

    @GetMapping("/me")
    public ApiResponse<LoginResponse.UserInfo> me() {
        LoginResponse.UserInfo userInfo = authService.me();
        if (userInfo == null) {
            return ApiResponse.error(ResultCode.UNAUTHORIZED);
        }
        return ApiResponse.success(userInfo);
    }
}
