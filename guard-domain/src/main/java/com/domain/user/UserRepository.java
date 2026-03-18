package com.domain.user;

import java.util.Optional;

/**
 * User Repository Port
 * 
 * 定義使用者存取的合約。實作將由 Infrastructure 層提供。
 */
public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User save(User user);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
