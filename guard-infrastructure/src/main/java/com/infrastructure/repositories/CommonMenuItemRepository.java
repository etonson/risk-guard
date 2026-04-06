package com.infrastructure.repositories;

import com.infrastructure.entities.MenuItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Common Menu Item Repository
 * <p>
 * Standard JPA repository for MenuItemEntity
 */
@Repository
public interface CommonMenuItemRepository extends JpaRepository<MenuItemEntity, Long> {
    
    /**
     * Find all items ordered by their sequence number
     */
    List<MenuItemEntity> findAllByOrderByOrderAsc();
    
    /**
     * Find an item by its logical menu ID
     */
    Optional<MenuItemEntity> findByMenuId(String menuId);
}
