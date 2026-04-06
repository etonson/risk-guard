package com.infrastructure.adapters;

import com.domain.menu.MenuItem;
import com.domain.menu.MenuItemRepository;
import com.infrastructure.entities.MenuItemEntity;
import com.infrastructure.repositories.CommonMenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JPA implementation of MenuItemRepository
 * <p>
 * Bridges the gap between infrastructure and domain
 */
@Repository
@RequiredArgsConstructor
public class JpaMenuItemRepositoryAdapter implements MenuItemRepository {

    private final CommonMenuItemRepository jpaRepository;

    @Override
    public List<MenuItem> findAllOrdered() {
        List<MenuItemEntity> entities = jpaRepository.findAllByOrderByOrderAsc();
        return buildMenuTree(entities);
    }

    @Override
    public Optional<MenuItem> findByMenuId(String menuId) {
        return jpaRepository.findByMenuId(menuId).map(this::toDomain);
    }

    private List<MenuItem> buildMenuTree(List<MenuItemEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Convert all entities to domain objects (without children yet)
        // Store in a map keyed by entity ID (Long) for parent lookup
        Map<Long, MenuItem> idMap = new HashMap<>();
        List<MenuItem> allItems = new ArrayList<>();
        
        for (MenuItemEntity entity : entities) {
            MenuItem item = toDomain(entity);
            idMap.put(entity.getId(), item);
            allItems.add(item);
        }

        // 2. Link children to parents and collect root items
        List<MenuItem> rootItems = new ArrayList<>();
        for (MenuItemEntity entity : entities) {
            MenuItem item = idMap.get(entity.getId());
            if (entity.getParentId() == null) {
                rootItems.add(item);
            } else {
                MenuItem parent = idMap.get(entity.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(item);
                } else {
                    // If parent not found, treat as root or log error
                    rootItems.add(item);
                }
            }
        }
        
        return rootItems;
    }

    private MenuItem toDomain(MenuItemEntity entity) {
        if (entity == null) return null;
        
        // 處理 meta: 包含 description
        Map<String, Object> meta = new HashMap<>();
        if (entity.getDescription() != null) {
            meta.put("description", entity.getDescription());
        }

        return MenuItem.builder()
                .id(entity.getMenuId())
                .name(entity.getName())
                .path(entity.getPath())
                .label(entity.getLabel())
                .icon(entity.getIcon())
                .order(entity.getOrder())
                .canDelete(entity.getCanDelete())
                .description(entity.getDescription())
                .meta(meta)
                .requiredRoles(parseSet(entity.getRequiredRoles()))
                .requiredPermissions(parseSet(entity.getRequiredPermissions()))
                .requireAllRoles(entity.getRequireAllRoles() != null && entity.getRequireAllRoles())
                .build();
    }

    private Set<String> parseSet(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}
