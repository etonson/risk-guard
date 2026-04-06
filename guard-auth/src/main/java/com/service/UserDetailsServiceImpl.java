package com.service;


import com.applications.user.UserQueryService;
import com.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import com.security.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


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
        Optional<User> userOptional = userQueryService.findByEmail(username)
                .or(() -> userQueryService.findByUsername(username));

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            log.debug("User found in database: {}, roles: {}, perms: {}", 
                    user.getUsername(), user.getRoleCodes(), user.getPermissions());

            // 🔥 Bridge Security and Domain: wrap domain user into security user
            return new SecurityUser(user);
        }

        log.warn("User not found in database: {}", username);
        throw new UsernameNotFoundException("User not found: " + username);
    }
}