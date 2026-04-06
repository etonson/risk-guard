package com.security;

import com.applications.auth.RefreshTokenCookieFactory;
import com.applications.auth.SecurityService;
import com.applications.auth.dto.AuthResult;
import com.applications.auth.dto.LoginRequest;
import com.applications.auth.dto.LoginResponse;
import com.applications.auth.dto.RegisterRequest;
import com.applications.user.UserQueryService;
import com.domain.user.User;
import com.domain.user.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import util.lang.InputValidator;

import java.util.List;
import java.util.Optional;

/**
 * Security Service Implementation
 * <p>
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
    private final UserQueryService userQueryService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResult login(LoginRequest req) {

        String principal = InputValidator.isNotEmpty(req.email()) ? req.email() : req.username();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(principal, req.password())
        );

        // Fetch user from Application Service
        User user = findUserByPrincipal(principal)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found: " + principal));

        return getAuthResult(resolvePrincipal(user), user);
    }

    @NonNull
    private AuthResult getAuthResult(String principal, User user) {
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        ResponseCookie refreshCookie = cookieFactory.createRefreshTokenCookie(refreshToken);

        ResponseCookie accessCookie = cookieFactory.createAccessTokenCookie(accessToken);

        LoginResponse response = LoginResponse.from(user, accessToken);

        return new AuthResult(response, refreshCookie.toString(), accessCookie.toString());
    }

    @Override
    public AuthResult register(RegisterRequest req) {
        if (userQueryService.existsByEmail(req.email())) {
            throw new RuntimeException("Email already exists: " + req.email());
        }

        User user = User.builder()
                .email(req.email())
                .username(req.username() != null ? req.username() : req.email().split("@")[0])
                .password(passwordEncoder.encode(req.password()))
                .status(UserStatus.ACTIVE)
                .build();

        user = userQueryService.save(user);

        String subject = user.getEmail();
        return getAuthResult(subject, user);
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
        String principal = jwtService.extractUsername(refreshToken);
        if (principal != null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(principal);
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                User user = findUserByPrincipal(principal)
                        .orElseThrow(() -> new RuntimeException("User not found: " + principal));
                String accessToken = jwtService.generateAccessToken(resolvePrincipal(user));

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
            return findUserByPrincipal(userDetails.getUsername()).orElse(null);
        }
        return null;
    }

    private Optional<User> findUserByPrincipal(String principal) {
        return userQueryService.findByEmail(principal)
                .or(() -> userQueryService.findByUsername(principal));
    }

    private String resolvePrincipal(User user) {
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            return user.getEmail();
        }
        return user.getUsername();
    }
}
