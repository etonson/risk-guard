package com.applications.auth;

import com.applications.auth.dto.AuthResult;
import com.applications.auth.dto.LoginRequest;
import com.applications.auth.dto.RegisterRequest;
import java.util.List;

/**
 * Security Service Port
 * <p>
 * 這是 Application 層定義的安全介面，具體實作由 guard-auth 提供。
 */
public interface SecurityService {
    AuthResult login(LoginRequest req);
    AuthResult register(RegisterRequest req);
    List<String> logout();
    AuthResult refresh(String refreshToken);
    Object getCurrentUser();
}
