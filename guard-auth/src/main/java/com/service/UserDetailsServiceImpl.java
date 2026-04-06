package com.service;


import com.applications.user.UserQueryService;
import com.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserQueryService userQueryService;

    public UserDetailsServiceImpl(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {

        log.debug("Attempting to load user: {}", username);

        // Try to find user by email or username
        Optional<User> userOptional = userQueryService.findByEmail(username);
        if (userOptional.isEmpty()) {
            userOptional = userQueryService.findByUsername(username);
        }

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            log.debug("User found in database: {}, roles: {}, perms: {}", 
                    user.getUsername(), user.getRoleCodes(), user.getPermissions());

            // Combine Role codes (ROLE_ prefix) and Permission codes
            List<SimpleGrantedAuthority> authorities = Stream.concat(
                    user.getRoleCodes().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)),
                    user.getPermissions().stream().map(SimpleGrantedAuthority::new)
            ).collect(Collectors.toList());

            return org.springframework.security.core.userdetails.User.builder()
                    .username(resolvePrincipal(user))
                    .password(user.getPassword())
                    .authorities(authorities)
                    .build();
        }

        log.warn("User not found in database: {}", username);
        throw new UsernameNotFoundException("User not found: " + username);
    }

    private String resolvePrincipal(User user) {
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            return user.getEmail();
        }
        return user.getUsername();
    }
}