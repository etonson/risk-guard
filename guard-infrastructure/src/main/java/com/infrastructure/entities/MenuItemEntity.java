package com.infrastructure.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Menu Item Entity
 * <p>
 * Database representation of a menu item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MENU_ITEM_")
public class MenuItemEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_")
    private Long id;

    @Column(name = "MENU_ID_", nullable = false, unique = true)
    private String menuId;

    @Column(name = "NAME_", nullable = false)
    private String name;

    @Column(name = "PATH_")
    private String path;

    @Column(name = "LABEL_")
    private String label;

    @Column(name = "ICON_")
    private String icon;

    @Column(name = "ORDER_")
    private Integer order;

    @Column(name = "CAN_DELETE_")
    private Boolean canDelete = true;

    @Column(name = "DESCRIPTION_", length = 500)
    private String description;

    @Column(name = "REQUIRED_ROLES_")
    private String requiredRoles; // Comma separated

    @Column(name = "REQUIRED_PERMISSIONS_")
    private String requiredPermissions; // Comma separated

    @Column(name = "REQUIRE_ALL_ROLES_")
    private Boolean requireAllRoles = false;

    @Column(name = "PARENT_ID_")
    private Long parentId;

    @Column(name = "CREATED_AT_", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT_")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
