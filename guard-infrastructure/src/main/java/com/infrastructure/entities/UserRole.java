package com.infrastructure.entities;

import com.domain.user.enums.DataScope;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * 角色實體
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SYS_ROLE_")
public class UserRole implements Serializable {

    /// 編號
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_")
    private Long id;

    /// 角色代碼
    @Column(name = "CODE_", nullable = false, unique = true)
    private String code;

    /// 角色名稱
    @Column(name = "NAME_", nullable = false)
    private String name;

    /// 數據範圍
    @Column(name = "DATA_SCOPE_")
    @Enumerated(EnumType.STRING)
    private DataScope dataScope;

    /// 權限集合
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "SYS_ROLE_PERMISSION_",
            joinColumns = @JoinColumn(name = "ROLE_ID_"),
            inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID_")
    )
    private Set<UserPermission> permissions;
}
