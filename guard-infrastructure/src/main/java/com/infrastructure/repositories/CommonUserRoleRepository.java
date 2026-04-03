package com.infrastructure.repositories;

import com.infrastructure.entities.UserRole;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonUserRoleRepository extends BaseRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);
}
