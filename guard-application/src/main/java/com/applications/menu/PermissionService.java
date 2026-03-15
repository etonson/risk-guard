package com.applications.menu;

import com.infrastructure.repositories.UserPermissionRepository;
import com.infrastructure.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Permission Service
 * 
 * Handles permission and role retrieval for the current user
 * 
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {
    
    private final UserRoleRepository userRoleRepository;
    private final UserPermissionRepository userPermissionRepository;
    
    /**
     * Get all roles for the current authenticated user
     */
    public Set<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return new HashSet<>();
        }
        
        // 从SecurityContext中获取authorities（快速方案）
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                // 移除 ROLE_ 前缀以获得干净的角色名
                .map(auth -> auth.replace("ROLE_", ""))
                .collect(Collectors.toSet());
        
        log.debug("Current user roles from SecurityContext: {}", roles);
        return roles;
    }
    
    /**
     * Get all permissions for the current authenticated user
     * 
     * Option B: 从数据库查询完整的权限信息（推荐用于生产环境）
     * 当前实现使用SecurityContext中的roles作为演示
     */
    public Set<String> getCurrentUserPermissions() {
        // TODO: 实现完整的权限查询逻辑
        // 1. 从SecurityContext获取username
        // 2. 根据username查询user ID
        // 3. 从数据库查询该用户的permissions
        
        // 演示实现：基于角色推断权限
        Set<String> roles = getCurrentUserRoles();
        Set<String> permissions = new HashSet<>();
        
        // 示例：不同角色拥有不同权限
        if (roles.contains("ADMIN")) {
            permissions.add("MENU_VIEW_ALL");
            permissions.add("BLOG_MANAGE");
            permissions.add("CATEGORY_MANAGE");
            permissions.add("MENU_MANAGE");
        } else if (roles.contains("USER")) {
            permissions.add("BLOG_VIEW");
            permissions.add("CATEGORY_VIEW");
            permissions.add("MENU_VIEW");
        }
        
        log.debug("Current user permissions: {}", permissions);
        return permissions;
    }
    
    /**
     * Check if current user has any of the required roles
     */
    public boolean hasAnyRole(Set<String> requiredRoles) {
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return true; // 无要求则通过
        }
        
        Set<String> userRoles = getCurrentUserRoles();
        return requiredRoles.stream().anyMatch(userRoles::contains);
    }
    
    /**
     * Check if current user has all required roles
     */
    public boolean hasAllRoles(Set<String> requiredRoles) {
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return true; // 无要求则通过
        }
        
        Set<String> userRoles = getCurrentUserRoles();
        return userRoles.containsAll(requiredRoles);
    }
    
    /**
     * Check if current user has any of the required permissions
     */
    public boolean hasAnyPermission(Set<String> requiredPermissions) {
        if (requiredPermissions == null || requiredPermissions.isEmpty()) {
            return true; // 无要求则通过
        }
        
        Set<String> userPermissions = getCurrentUserPermissions();
        return requiredPermissions.stream().anyMatch(userPermissions::contains);
    }
    
    /**
     * Check if current user has all required permissions
     */
    public boolean hasAllPermissions(Set<String> requiredPermissions) {
        if (requiredPermissions == null || requiredPermissions.isEmpty()) {
            return true; // 无要求则通过
        }
        
        Set<String> userPermissions = getCurrentUserPermissions();
        return userPermissions.containsAll(requiredPermissions);
    }
}

