package com.applications.menu;

import com.domain.menu.MenuItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Menu Permission Filter
 * 
 * Handles recursive menu filtering based on user permissions and roles
 * 
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MenuPermissionFilter {
    
    private final PermissionService permissionService;
    
    /**
     * Filter menu items based on user permissions
     * 
     * Filters both the menu item itself and its children recursively
     * Removes parent menus if all children are filtered out
     */
    public List<MenuItem> filterMenuItems(List<MenuItem> menuItems) {
        List<MenuItem> filteredMenu = new ArrayList<>();
        
        for (MenuItem item : menuItems) {
            if (hasPermissionForMenuItem(item)) {
                List<MenuItem> filteredChildren = filterMenuChildren(item.getChildren());
                
                MenuItem filteredItem = MenuItem.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .path(item.getPath())
                        .label(item.getLabel())
                        .icon(item.getIcon())
                        .order(item.getOrder())
                        .canDelete(item.getCanDelete())
                        .description(item.getDescription())
                        .meta(item.getMeta())
                        .children(filteredChildren)
                        .requiredRoles(item.getRequiredRoles())
                        .requiredPermissions(item.getRequiredPermissions())
                        .requireAllRoles(item.isRequireAllRoles())
                        .build();
                
                // 只有当至少有一个子菜单或菜单项本身无子菜单时才添加
                if (filteredChildren == null || 
                    filteredChildren.isEmpty() || 
                    !menuItems.isEmpty()) {
                    filteredMenu.add(filteredItem);
                }
            }
        }
        
        return filteredMenu;
    }
    
    /**
     * Filter menu children recursively
     */
    private List<MenuItem> filterMenuChildren(List<MenuItem> children) {
        if (children == null || children.isEmpty()) {
            return children;
        }
        
        List<MenuItem> filteredChildren = new ArrayList<>();
        
        for (MenuItem child : children) {
            if (hasPermissionForMenuItem(child)) {
                List<MenuItem> filteredGrandchildren = filterMenuChildren(child.getChildren());
                
                MenuItem filteredChild = MenuItem.builder()
                        .id(child.getId())
                        .name(child.getName())
                        .path(child.getPath())
                        .label(child.getLabel())
                        .icon(child.getIcon())
                        .order(child.getOrder())
                        .canDelete(child.getCanDelete())
                        .description(child.getDescription())
                        .meta(child.getMeta())
                        .children(filteredGrandchildren)
                        .requiredRoles(child.getRequiredRoles())
                        .requiredPermissions(child.getRequiredPermissions())
                        .requireAllRoles(child.isRequireAllRoles())
                        .build();
                filteredChildren.add(filteredChild);
            }
        }
        
        return filteredChildren.isEmpty() ? null : filteredChildren;
    }
    
    /**
     * Check if the current user has permission to access this menu item
     * 
     * Supports both role-based and permission-based access control
     * - If requireAllRoles is true: user must have ALL required roles
     * - If requireAllRoles is false: user must have ANY required role
     * - Permissions are checked with OR logic (has any of the required permissions)
     */
    private boolean hasPermissionForMenuItem(MenuItem item) {
        // 如果没有权限要求，允许访问
        if ((item.getRequiredRoles() == null || item.getRequiredRoles().isEmpty()) &&
            (item.getRequiredPermissions() == null || item.getRequiredPermissions().isEmpty())) {
            return true;
        }
        
        // 检查角色
        boolean rolesPass = checkRolePermission(
                item.getRequiredRoles(), 
                item.isRequireAllRoles()
        );
        
        // 检查权限
        boolean permissionsPass = checkPermissionRequirement(item.getRequiredPermissions());
        
        // 需要同时通过角色和权限检查
        return rolesPass && permissionsPass;
    }
    
    /**
     * Check role requirements
     */
    private boolean checkRolePermission(Set<String> requiredRoles, boolean requireAll) {
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            return true;
        }
        
        if (requireAll) {
            return permissionService.hasAllRoles(requiredRoles);
        } else {
            return permissionService.hasAnyRole(requiredRoles);
        }
    }
    
    /**
     * Check permission requirements (OR logic: has any of the required permissions)
     */
    private boolean checkPermissionRequirement(Set<String> requiredPermissions) {
        if (requiredPermissions == null || requiredPermissions.isEmpty()) {
            return true;
        }
        
        return permissionService.hasAnyPermission(requiredPermissions);
    }
}

