package com.security;

import com.applications.auth.RefreshTokenCookieFactory;
import com.applications.auth.SecurityService;
import com.applications.auth.dto.AuthResult;
import com.applications.auth.dto.LoginRequest;
import com.applications.auth.dto.LoginResponse;
import com.applications.auth.dto.RegisterRequest;
import com.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Security Service Implementation
 * 
 * 負責處理所有與安全框架（Spring Security, JWT）相關的具體實作。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenCookieFactory cookieFactory;
    private final UserDetailsService userDetailsService;

    @Override
    public AuthResult login(LoginRequest req) {
        String principal = (req.email() != null && !req.email().isEmpty()) ? req.email() : req.username();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(principal, req.password())
        );

        // 這裡暫時模擬，實際應從 UserRepository 獲取並轉為 Domain User
        User user = User.builder()
                .id(1L)
                .email(req.email())
                .username(req.username() != null ? req.username() : req.email())
                .build();

        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        ResponseCookie refreshCookie = cookieFactory.createRefreshTokenCookie(refreshToken);
        ResponseCookie accessCookie = cookieFactory.createAccessTokenCookie(accessToken);

        LoginResponse response = LoginResponse.from(user, accessToken);

        return new AuthResult(response, refreshCookie.toString(), accessCookie.toString());
    }

    @Override
    public AuthResult register(RegisterRequest req) {
        // 模擬註冊邏輯
        User user = User.builder()
                .id(1L)
                .email(req.email())
                .username(req.username() != null ? req.username() : req.email())
                .build();

        String subject = req.email();
        String accessToken = jwtService.generateAccessToken(subject);
        String refreshToken = jwtService.generateRefreshToken(subject);

        ResponseCookie refreshCookie = cookieFactory.createRefreshTokenCookie(refreshToken);
        ResponseCookie accessCookie = cookieFactory.createAccessTokenCookie(accessToken);

        LoginResponse response = LoginResponse.from(user, accessToken);

        return new AuthResult(response, refreshCookie.toString(), accessCookie.toString());
    }

    @Override
    public List<String> logout() {
        return List.of(
                cookieFactory.getClearRefreshTokenCookie().toString(),
                cookieFactory.getClearAccessTokenCookie().toString()
        );
    }

    @Override
    public AuthResult refresh(String refreshToken) {
        String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                String accessToken = jwtService.generateAccessToken(userEmail);

                User user = User.builder()
                        .id(1L)
                        .email(userEmail)
                        .username(userEmail)
                        .build();

                LoginResponse response = LoginResponse.from(user, accessToken);
                ResponseCookie accessCookie = cookieFactory.createAccessTokenCookie(accessToken);

                return new AuthResult(response, null, accessCookie.toString());
            }
        }
        throw new RuntimeException("Invalid refresh token");
    }

    @Override
    public Object getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            // 實際應從 DB 獲取 User Domain Object
            return User.builder()
                    .id(1L)
                    .email(userDetails.getUsername())
                    .username(userDetails.getUsername())
                    .build();
        }
        return null;
    }
}
