package com.applications.auth;

import com.applications.auth.dto.AuthResult;
import com.applications.auth.dto.LoginRequest;
import com.applications.auth.dto.RegisterRequest;
import com.applications.auth.dto.UserInfo;
import com.applications.common.dto.ApiResponse;
import com.domain.user.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Authentication Application Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthApplicationService {

    private final SecurityService securityService;

    public AuthResult login(LoginRequest req) {
        log.info("Attempting login for: {}", req.username() != null ? req.username() : req.email());
        return securityService.login(req);
    }

    public AuthResult register(RegisterRequest req) {
        log.info("Attempting registration for: {}", req.email());
        return securityService.register(req);
    }

    public ApiResponse<Void> logout(HttpServletResponse response) {
        securityService.logout().forEach(cookie -> response.addHeader(HttpHeaders.SET_COOKIE, cookie));
        return ApiResponse.success(null, "登出成功");
    }

    public AuthResult refresh(String refreshToken) {
        return securityService.refresh(refreshToken);
    }

    public UserInfo me() {
        Object principal = securityService.getCurrentUser();
        
        if (principal instanceof User user) {
            return new UserInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getUsername(),
                    List.of("USER")
            );
        }
        
        return null;
    }
}
