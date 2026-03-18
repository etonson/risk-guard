package com.domain.user;

import java.util.Set;

public interface UserPermissionRepository {
    Set<String> findPermissionNamesByUserId(Long userId);
}
