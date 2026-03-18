package com.infrastructure.repositories;

import com.infrastructure.entities.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * User Permission Repository
 * 
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long>, com.domain.user.UserPermissionRepository {
    
    /**
     * Find all permissions for a specific user
     */
    List<UserPermission> findByUserId(Long userId);
    
    /**
     * Get all permission names for a user
     */
    @Query("SELECT up.permissionName FROM UserPermission up WHERE up.userId = :userId")
    Set<String> findPermissionNamesByUserId(Long userId);
}

