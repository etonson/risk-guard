package com.applications.menu;

import com.domain.user.UserPermissionRepository;
import com.domain.user.UserRoleRepository;
import com.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

/**
 * Permission Service
 * <p>
 * Handles permission and role retrieval for a given user identifier
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
    private final UserRepository userRepository;
    
    public Set<String> getCurrentUserRoles(String identifier) {
        if (identifier == null) return new HashSet<>();
        
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .map(user -> userRoleRepository.findRoleNamesByUserId(user.getId()))
                .orElse(new HashSet<>());
    }
    
    public Set<String> getCurrentUserPermissions(String identifier) {
        if (identifier == null) return new HashSet<>();
        
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier))
                .map(user -> userPermissionRepository.findPermissionNamesByUserId(user.getId()))
                .orElse(new HashSet<>());
    }
    
    public boolean hasAnyRole(String identifier, Set<String> requiredRoles) {
        if (requiredRoles == null || requiredRoles.isEmpty()) return true;
        Set<String> userRoles = getCurrentUserRoles(identifier);
        return requiredRoles.stream().anyMatch(userRoles::contains);
    }
    
    public boolean hasAllRoles(String identifier, Set<String> requiredRoles) {
        if (requiredRoles == null || requiredRoles.isEmpty()) return true;
        Set<String> userRoles = getCurrentUserRoles(identifier);
        return userRoles.containsAll(requiredRoles);
    }
    
    public boolean hasAnyPermission(String identifier, Set<String> requiredPermissions) {
        if (requiredPermissions == null || requiredPermissions.isEmpty()) return true;
        Set<String> userPermissions = getCurrentUserPermissions(identifier);
        return requiredPermissions.stream().anyMatch(userPermissions::contains);
    }
    
    public boolean hasAllPermissions(String identifier, Set<String> requiredPermissions) {
        if (requiredPermissions == null || requiredPermissions.isEmpty()) return true;
        Set<String> userPermissions = getCurrentUserPermissions(identifier);
        return userPermissions.containsAll(requiredPermissions);
    }
}
