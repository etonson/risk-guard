package com.applications.menu;
import com.domain.menu.MenuItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Menu Config Provider
 * <p>
 * Provides menu configuration
 * Can be extended to load from database or YAML config files
 * 
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Component
@Slf4j
public class MenuConfigProvider {
    
    /**
     * Get the complete menu configuration with permission requirements
     * 
     * This is a hard-coded configuration for now
     * Can be extended to load from database or YAML files
     */
    public List<MenuItem> getMenuConfiguration() {
        List<MenuItem> menuItems = new ArrayList<>();
        
        // Blog Posts - available to all authenticated users
        menuItems.add(MenuItem.builder()
                .id("blog_posts")
                .name("blog_posts")
                .label("Blog Posts")
                .path("/blog-posts")
                .icon("article")
                .order(1)
                .canDelete(true)
                .description("View and manage blog posts")
                .meta(Map.of("description", "Blog management"))
                .requiredRoles(Set.of("USER", "ADMIN"))  // Both USER and ADMIN can see (OR logic)
                .requiredPermissions(Set.of("BLOG_VIEW"))
                .requireAllRoles(false)  // OR logic - has any role
                .build());
        
        // Categories - available to all authenticated users
        menuItems.add(MenuItem.builder()
                .id("categories")
                .name("categories")
                .label("Categories")
                .path("/categories")
                .icon("category")
                .order(2)
                .canDelete(true)
                .description("Manage categories")
                .meta(Map.of("description", "Category management"))
                .requiredRoles(Set.of("USER", "ADMIN"))
                .requiredPermissions(Set.of("CATEGORY_VIEW"))
                .requireAllRoles(false)
                .build());
        
        // Menu Management - only for ADMIN
        menuItems.add(MenuItem.builder()
                .id("menu-management")
                .name("menu-management")
                .label("Menu Management")
                .path("/menu-management")
                .icon("settings")
                .order(3)
                .canDelete(false)
                .description("Configure application menus")
                .meta(Map.of("description", "Menu settings"))
                .requiredRoles(Set.of("ADMIN"))  // Only ADMIN
                .requiredPermissions(Set.of("MENU_MANAGE"))
                .requireAllRoles(true)  // AND logic - must have all roles
                .build());
        
        // System Settings - only for ADMIN
        menuItems.add(MenuItem.builder()
                .id("system-settings")
                .name("system-settings")
                .label("System Settings")
                .path("/settings")
                .icon("cog")
                .order(4)
                .canDelete(false)
                .description("Configure system settings")
                .meta(Map.of("description", "System configuration"))
                .requiredRoles(Set.of("ADMIN"))
                .requireAllRoles(true)
                .build());
        
        log.debug("Loaded menu configuration with {} items", menuItems.size());
        return menuItems;
    }
    
    /**
     * Get a specific menu item by ID
     */
    public Optional<MenuItem> getMenuItemById(String id) {
        return getMenuConfiguration().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }
}

