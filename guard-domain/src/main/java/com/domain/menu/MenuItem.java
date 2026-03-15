package com.domain.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Menu Item Domain Entity
 * 
 * Represents a menu item in the application navigation
 * Supports permission-based access control
 * 
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    private String id;
    private String name;
    private String path;
    private String label;
    private String icon;
    private Integer order;
    private Boolean canDelete;
    private String description;
    private Map<String, Object> meta;
    private List<MenuItem> children;
    
    // 权限控制字段
    private Set<String> requiredRoles;      // 所需角色集合
    private Set<String> requiredPermissions; // 所需权限集合
    @Builder.Default
    private boolean requireAllRoles = false; // true=AND逻辑(全部满足), false=OR逻辑(任一满足)
}

