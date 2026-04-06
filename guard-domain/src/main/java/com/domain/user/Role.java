package com.domain.user;

import com.domain.user.enums.DataScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Role Domain Entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private String code;        // 角色代碼，如 "ADMIN"
    private String name;        // 角色名稱
    private Set<Permission> permissions;
    private DataScope dataScope; // 數據權限範疇
}