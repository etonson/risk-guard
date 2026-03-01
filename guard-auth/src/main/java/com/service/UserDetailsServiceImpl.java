package com.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    // 關鍵！解決循環依賴問題
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("DEBUG: Attempting to load user: " + username);

        String encodedPassword = passwordEncoder.encode("123456789");

        log.debug("DEBUG: Generated password hash for user: " + username);

        // Return UserDetails with the provided username
        return User.builder()
                .username(username)
                .password(encodedPassword)
                .authorities("ROLE_USER")
                .build();
    }
}