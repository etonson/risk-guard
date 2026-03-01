package com.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(@Nonnull HttpServletRequest request) {
        String path = request.getRequestURI();
        log.debug("JWT Filter - shouldNotFilter check for path: {}", path);

        // Skip JWT filter for public endpoints that don't need any authentication attempt
        // Note: path includes context path (/api), so we check if it ends with or contains the endpoint
        boolean skip = path.endsWith("/auth/login") || path.endsWith("/auth/register");

        log.debug("JWT Filter - shouldNotFilter result: {}", skip);
        return skip;
    }

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {

        final String jwt = getJwtFromRequest(request);
        final String userEmail;

        log.debug("JWT Filter - Path: {}, JWT present: {}", request.getRequestURI(), jwt != null);

        if (jwt == null) {
            log.debug("JWT Filter - No JWT token found, continuing without authentication");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            userEmail = jwtService.extractUsername(jwt);
            log.debug("JWT Filter - Extracted username: {}", userEmail);
        } catch (Exception e) {
            // Token invalid or expired
            log.debug("JWT Filter - Invalid token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                log.debug("JWT Filter - Loaded user details: {}", userDetails.getUsername());

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("JWT Filter - Authentication set successfully for user: {}", userEmail);
                } else {
                    log.debug("JWT Filter - Token validation failed for user: {}", userEmail);
                }
            } catch (Exception e) {
                // If user loading fails (e.g., database issue), continue without auth
                log.error("JWT Filter - Failed to load user or set authentication: {}", e.getMessage());
            }
        } else {
            log.debug("JWT Filter - userEmail is null or authentication already set");
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        // First, check Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // Second, check access_token cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
