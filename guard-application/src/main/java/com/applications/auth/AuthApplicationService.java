package com.applications.auth;

import com.infrastructure.entities.CommonUser;
import com.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Authentication Application Service
 *
 * @Author: Eton.Lin
 * @Date: 2026/1/4 下午9:13
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthApplicationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenCookieFactory cookieFactory;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthResult login(LoginRequest req) {
        // Determine principal (email or username)
        String principal = (req.email() != null && !req.email().isEmpty()) ? req.email() : req.username();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        principal, req.password()
                )
        );

        CommonUser user = new CommonUser();
        user.setId(1L); // TODO: Get from database
        user.setEmail(req.email());
        user.setUsername(req.username() != null ? req.username() : req.email());

        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        ResponseCookie refreshCookie = cookieFactory.createRefreshTokenCookie(refreshToken);
        ResponseCookie accessCookie = cookieFactory.createAccessTokenCookie(accessToken);

        LoginResponse response = LoginResponse.from(user, accessToken);

        return new AuthResult(response, refreshCookie.toString(), accessCookie.toString());
    }

    public AuthResult register(RegisterRequest req) {
        // For now, just create a user and return tokens
        CommonUser user = new CommonUser();
        user.setId(1L); // TODO: Get from database after saving
        user.setEmail(req.email());
        user.setUsername(req.username() != null ? req.username() : req.email());

        String subject = req.email();
        String accessToken = jwtService.generateAccessToken(subject);
        String refreshToken = jwtService.generateRefreshToken(subject);

        ResponseCookie refreshCookie = cookieFactory.createRefreshTokenCookie(refreshToken);
        ResponseCookie accessCookie = cookieFactory.createAccessTokenCookie(accessToken);

        LoginResponse response = LoginResponse.from(user, accessToken);

        return new AuthResult(response, refreshCookie.toString(), accessCookie.toString());
    }

    public List<String> logout() {
        return List.of(
                cookieFactory.getClearRefreshTokenCookie().toString(),
                cookieFactory.getClearAccessTokenCookie().toString()
        );
    }

    public AuthResult refresh(String refreshToken) {
        String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                String accessToken = jwtService.generateAccessToken(userEmail);

                CommonUser user = new CommonUser();
                user.setId(1L); // TODO: Get from database
                user.setEmail(userEmail);
                user.setUsername(userEmail);

                LoginResponse response = LoginResponse.from(user, accessToken);

                ResponseCookie accessCookie = cookieFactory.createAccessTokenCookie(accessToken);

                return new AuthResult(response, null, accessCookie.toString());
            }
        }
        throw new RuntimeException("Invalid refresh token");
    }

    public LoginResponse.UserInfo me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        log.debug("AuthService.me() - Authentication: {}", auth);

        // Check if authentication exists and is not anonymous
        if (auth == null) {
            log.debug("AuthService.me() - Authentication is null");
            return null;
        }

        log.debug("AuthService.me() - isAuthenticated: {}", auth.isAuthenticated());
        log.debug("AuthService.me() - Authentication class: {}", auth.getClass().getName());

        if (!auth.isAuthenticated()) {
            log.debug("AuthService.me() - Not authenticated");
            return null;
        }

        Object principal = auth.getPrincipal();
        log.debug("AuthService.me() - Principal: {}", principal);
        log.debug("AuthService.me() - Principal class: {}", principal != null ? principal.getClass().getName() : "null");

        // Spring Security uses "anonymousUser" string for anonymous authentication
        if (principal == null) {
            log.debug("AuthService.me() - Principal is null");
            return null;
        }

        if (principal.equals("anonymousUser")) {
            log.debug("AuthService.me() - Principal is anonymousUser");
            return null;
        }

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            log.debug("AuthService.me() - UserDetails username: {}", username);

            // TODO: Fetch user details from database
            CommonUser user = new CommonUser();
            user.setId(1L);
            user.setEmail(username);
            user.setUsername(username);

            return new LoginResponse.UserInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getUsername(),
                    List.of("USER")
            );
        }

        // If principal is a string (shouldn't happen in our case, but safety check)
        if (principal instanceof String username) {
            log.debug("AuthService.me() - Principal is String: {}", username);

            CommonUser user = new CommonUser();
            user.setId(1L);
            user.setEmail(username);
            user.setUsername(username);

            return new LoginResponse.UserInfo(
                    user.getId(),
                    user.getEmail(),
                    user.getUsername(),
                    List.of("USER")
            );
        }

        log.debug("AuthService.me() - Principal type not recognized");
        return null;
    }
}
