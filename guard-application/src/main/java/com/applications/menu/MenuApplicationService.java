package com.applications.menu;

import com.domain.menu.MenuItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Menu Application Service
 * 
 * Responsible for menu business logic and retrieval
 * Integrates permission checking and filtering
 * 
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MenuApplicationService {
    
    private final MenuConfigProvider menuConfigProvider;
    private final MenuPermissionFilter menuPermissionFilter;
    
    /**
     * Get user menu based on current user's roles and permissions
     * 
     * Steps:
     * 1. Get all menu items from configuration
     * 2. Filter menu items based on user permissions
     * 3. Return filtered menu to the user
     */
    public List<MenuItem> getUserMenu() {
        log.info("Fetching menu for current user");
        
        // 1. Get complete menu configuration
        List<MenuItem> allMenuItems = menuConfigProvider.getMenuConfiguration();
        
        // 2. Filter based on user permissions
        List<MenuItem> filteredMenu = menuPermissionFilter.filterMenuItems(allMenuItems);
        
        log.info("Returned {} menu items after permission filtering", filteredMenu.size());
        return filteredMenu;
    }
}

