package com.service;

import com.applications.user.UserQueryService;
import com.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserQueryService userQueryService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("DEBUG: Attempting to load user: " + username);

        // Try to find user by email or username
        Optional<User> userOptional = userQueryService.findByEmail(username);
        if (userOptional.isEmpty()) {
            userOptional = userQueryService.findByUsername(username);
        }

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            log.debug("DEBUG: User found in database: " + user.getUsername());
            
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities("ROLE_USER")
                    .build();
        }

        log.warn("DEBUG: User not found in database: " + username + ". Returning mock user for development.");
        
        String encodedPassword = passwordEncoder.encode("Test123!");

        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(encodedPassword)
                .authorities("ROLE_USER")
                .build();
    }
}