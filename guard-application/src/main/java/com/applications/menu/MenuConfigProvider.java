package com.applications.menu;
import com.domain.menu.MenuItem;
import com.domain.menu.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * Menu Config Provider
 * <p>
 * Provides menu configuration
 * Loads from database via MenuItemRepository
 * 
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MenuConfigProvider {
    
    private final MenuItemRepository menuItemRepository;
    
    /**
     * Get the complete menu configuration from database
     */
    public List<MenuItem> getMenuConfiguration() {
        log.debug("Loading menu configuration from database");
        List<MenuItem> menuItems = menuItemRepository.findAllOrdered();
        log.debug("Loaded {} menu items from database", menuItems.size());
        return menuItems;
    }
    
    /**
     * Get a specific menu item by ID
     */
    public Optional<MenuItem> getMenuItemById(String id) {
        return menuItemRepository.findByMenuId(id);
    }
}