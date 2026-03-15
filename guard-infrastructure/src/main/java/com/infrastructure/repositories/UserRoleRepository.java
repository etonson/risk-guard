package com.infrastructure.repositories;

import com.infrastructure.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * User Role Repository
 * 
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    
    /**
     * Find all roles for a specific user
     */
    List<UserRole> findByUserId(Long userId);
    
    /**
     * Get all role names for a user
     */
    @Query("SELECT ur.roleName FROM UserRole ur WHERE ur.userId = :userId")
    Set<String> findRoleNamesByUserId(Long userId);
}

