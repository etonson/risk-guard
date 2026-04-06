package com.domain.user;

import com.domain.user.enums.PermissionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Permission Domain Entity
 * 最小功能授權單位
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    private String code;        // 權限代碼，如 "user:view"
    private String name;        // 權限名稱
    private PermissionType type; // 類型：MENU, BUTTON, API
    private String description;
}
