package com.domain.user;

import com.domain.user.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User Domain Entity
 * <p>
 * 這是純淨的業務實體，不含任何 JPA 或框架註解。
 * 遵循六邊形架構，Domain 層應與外部技術細節隔離。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String realName;
    private UserStatus status;
    private Set<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 攤平所有角色擁有的權限代碼
     */
    public Set<String> getPermissions() {
        if (roles == null) return Set.of();
        return roles.stream()
                .filter(r -> r.getPermissions() != null)
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }

    /**
     * 攤平所有角色的代碼
     */
    public Set<String> getRoleCodes() {
        if (roles == null) return Set.of();
        return roles.stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());
    }
}
