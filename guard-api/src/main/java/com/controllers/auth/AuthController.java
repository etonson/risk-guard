package com.controllers.auth;


import com.applications.auth.AuthApplicationService;
import com.applications.auth.dto.LoginRequest;
import com.applications.auth.dto.LoginResponse;
import com.applications.auth.dto.RegisterRequest;
import com.applications.common.dto.ApiResponse;
import com.applications.common.dto.ResultCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller - 極簡版
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthApplicationService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest req, HttpServletResponse response) {
        return authService.login(req).writeTo(response);
    }

    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@RequestBody RegisterRequest req, HttpServletResponse response) {
        return authService.register(req).writeTo(response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse response) {
        return authService.logout(response);
    }

    @PostMapping("/refresh")
    public ApiResponse<LoginResponse> refresh(
            @CookieValue(name = "refresh_token") String token,
            HttpServletResponse response
    ) {
        return authService.refresh(token).writeTo(response);
    }

    @GetMapping("/me")
    public ApiResponse<LoginResponse.UserInfo> me() {
        LoginResponse.UserInfo userInfo = authService.me();
        return userInfo != null ? ApiResponse.success(userInfo) : ApiResponse.error(ResultCode.UNAUTHORIZED);
    }
}
