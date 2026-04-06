package com.applications.auth;

import com.applications.auth.dto.AuthResult;
import com.applications.auth.dto.LoginRequest;
import com.applications.auth.dto.RegisterRequest;
import com.applications.auth.dto.UserInfo;
import com.applications.common.exception.UnauthorizedException;
import com.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * Logout - 獲取需要寫入 Header 的 Cookie 列表
     */
    public List<String> logout() {
        return securityService.logout();
    }

    public AuthResult refresh(String refreshToken) {
        return securityService.refresh(refreshToken);
    }
}
