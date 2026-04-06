package com.domain.menu;

import java.util.List;
import java.util.Optional;

/**
 * Menu Item Repository Interface
 * <p>
 * Defines the contract for menu persistence and retrieval
 */
public interface MenuItemRepository {
    
    /**
     * Get all menu items sorted by order
     */
    List<MenuItem> findAllOrdered();
    
    /**
     * Get a specific menu item by its logical ID
     */
    Optional<MenuItem> findByMenuId(String menuId);
}
