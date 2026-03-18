package com.domain.user;

import java.util.Set;

public interface UserRoleRepository {
    Set<String> findRoleNamesByUserId(Long userId);
}
