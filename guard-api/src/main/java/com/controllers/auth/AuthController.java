package com.controllers.auth;

import com.applications.auth.*;
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
    public LoginResponse login(
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

        return result.response();
    }

    @PostMapping("/register")
    public LoginResponse register(
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

        return result.response();
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        List<String> cookies = authService.logout();
        for (String cookie : cookies) {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie);
        }
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }

        try {
            AuthResult result = authService.refresh(refreshToken);

            if (result.refreshCookie() != null) {
                response.addHeader(HttpHeaders.SET_COOKIE, result.refreshCookie());
            }
            if (result.accessTokenCookie() != null) {
                response.addHeader(HttpHeaders.SET_COOKIE, result.accessTokenCookie());
            }

            return result.response();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }

    @GetMapping("/me")
    public LoginResponse.UserInfo me(HttpServletResponse response) {
        LoginResponse.UserInfo userInfo = authService.me();
        if (userInfo == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
        return userInfo;
    }
}
