package com.infrastructure.repositories;

import com.infrastructure.entities.UserPermission;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonUserPermissionRepository extends BaseRepository<UserPermission, Long> {
    List<UserPermission> findByUserId(Long userId);
}
