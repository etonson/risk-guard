package com.applications.menu;

import com.domain.user.User;
import com.domain.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
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
    
    private final UserRepository userRepository;
    
    public Set<String> getCurrentUserRoles(String identifier) {
        if (identifier == null) return Set.of();
        
        return findUser(identifier)
                .map(User::getRoleCodes)
                .orElse(Set.of());
    }
    
    public Set<String> getCurrentUserPermissions(String identifier) {
        if (identifier == null) return Set.of();
        
        return findUser(identifier)
                .map(User::getPermissions)
                .orElse(Set.of());
    }

    private Optional<User> findUser(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByUsername(identifier));
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
