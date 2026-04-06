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

    /// 編號
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_")
    private Long id;

    /// 選單編號
    @Column(name = "MENU_ID_", nullable = false, unique = true)
    private String menuId;

    /// 名稱
    @Column(name = "NAME_", nullable = false)
    private String name;

    /// 路徑
    @Column(name = "PATH_")
    private String path;

    /// 標籤
    @Column(name = "LABEL_")
    private String label;

    /// 圖示
    @Column(name = "ICON_")
    private String icon;

    /// 排序
    @Column(name = "ORDER_")
    private Integer order;

    /// 是否可刪除
    @Column(name = "CAN_DELETE_")
    private Boolean canDelete = true;

    /// 描述
    @Column(name = "DESCRIPTION_", length = 500)
    private String description;

    /// 需具備的角色
    @Column(name = "REQUIRED_ROLES_")
    private String requiredRoles;

    /// 需具備的權限 (逗號分隔)
    @Column(name = "REQUIRED_PERMISSIONS_")
    private String requiredPermissions;

    /// 是否需具備所有角色
    @Column(name = "REQUIRE_ALL_ROLES_")
    private Boolean requireAllRoles = false;

    /// 父級選單編號
    @Column(name = "PARENT_ID_")
    private Long parentId;

    /// 建立時間
    @Column(name = "CREATED_AT_", updatable = false)
    private LocalDateTime createdAt;

    /// 更新時間
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
