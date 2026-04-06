package com.applications.user;

import com.domain.user.User;
import com.domain.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * User Query Service
 * <p>
 * 應用層服務，負責讀取使用者資訊。
 * 安全模組 (guard-auth) 應透過此服務與 Domain 層互動，而不是直接操作 Repository。
 */
@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
}
