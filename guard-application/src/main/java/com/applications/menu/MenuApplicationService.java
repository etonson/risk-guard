package com.applications.menu;

import com.applications.auth.SecurityService;
import com.domain.menu.MenuItem;
import com.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Menu Application Service
 * 
 * Responsible for menu business logic and retrieval
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MenuApplicationService {
    
    private final MenuConfigProvider menuConfigProvider;
    private final MenuFilterService menuFilterService;
    private final SecurityService securityService;
    
    public List<MenuItem> getUserMenu() {
        log.info("Fetching menu for current user");
        
        Object principal = securityService.getCurrentUser();
        String identifier = null;
        
        if (principal instanceof User user) {
            identifier = user.getEmail();
        }
        
        // 1. Get complete menu configuration
        List<MenuItem> allMenuItems = menuConfigProvider.getMenuConfiguration();
        
        // 2. Filter based on user permissions
        List<MenuItem> filteredMenu = menuFilterService.filterMenuItems(allMenuItems, identifier);
        
        log.info("Returned {} menu items after permission filtering", filteredMenu.size());
        return filteredMenu;
    }
}
