package com.applications.menu;

import com.domain.menu.MenuItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Menu Filter Service
 * <p>
 * Handles recursive menu filtering based on user permissions and roles
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MenuFilterService {
    
    private final PermissionService permissionService;
    
    public List<MenuItem> filterMenuItems(List<MenuItem> menuItems, String userIdentifier) {
        List<MenuItem> filteredMenu = new ArrayList<>();
        
        for (MenuItem item : menuItems) {
            if (hasPermissionForMenuItem(item, userIdentifier)) {
                List<MenuItem> filteredChildren = filterMenuChildren(item.getChildren(), userIdentifier);
                
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
                
                filteredMenu.add(filteredItem);
            }
        }
        
        return filteredMenu;
    }
    
    private List<MenuItem> filterMenuChildren(List<MenuItem> children, String identifier) {
        if (children == null || children.isEmpty()) return children;
        
        List<MenuItem> filteredChildren = new ArrayList<>();
        for (MenuItem child : children) {
            if (hasPermissionForMenuItem(child, identifier)) {
                List<MenuItem> filteredGrandchildren = filterMenuChildren(child.getChildren(), identifier);
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
    
    private boolean hasPermissionForMenuItem(MenuItem item, String identifier) {
        if ((item.getRequiredRoles() == null || item.getRequiredRoles().isEmpty()) &&
            (item.getRequiredPermissions() == null || item.getRequiredPermissions().isEmpty())) {
            return true;
        }
        
        boolean rolesPass = checkRolePermission(identifier, item.getRequiredRoles(), item.isRequireAllRoles());
        boolean permissionsPass = checkPermissionRequirement(identifier, item.getRequiredPermissions());
        
        return rolesPass && permissionsPass;
    }
    
    private boolean checkRolePermission(String identifier, Set<String> requiredRoles, boolean requireAll) {
        if (requiredRoles == null || requiredRoles.isEmpty()) return true;
        if (requireAll) {
            return permissionService.hasAllRoles(identifier, requiredRoles);
        } else {
            return permissionService.hasAnyRole(identifier, requiredRoles);
        }
    }
    
    private boolean checkPermissionRequirement(String identifier, Set<String> requiredPermissions) {
        if (requiredPermissions == null || requiredPermissions.isEmpty()) return true;
        return permissionService.hasAnyPermission(identifier, requiredPermissions);
    }
}
