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
import util.lang.InputValidator;

import java.util.List;

/**
 * Authentication Application Service
 *
 *
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthApplicationService {

    private final SecurityService securityService;

    public AuthResult login(LoginRequest req) {
        log.info("Attempting login for: {}", InputValidator.isNotEmpty(req.username()) ? req.username() : req.email());
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
        try {
            return securityService.refresh(refreshToken);
        } catch (Exception e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            throw new UnauthorizedException("無效或已過期的 Refresh Token");
        }
    }

    public UserInfo me() {
        Object principal = securityService.getCurrentUser();
        
        if (principal instanceof User user) {
            return new UserInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getUsername(),
                    user.getRoleCodes(),
                    user.getPermissions()
            );
        }
        
        throw new UnauthorizedException("未授權或 Session 已過期");
    }
}
