package com.infrastructure.entities;

import com.domain.user.enums.PermissionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 權限實體
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SYS_PERMISSION_")
public class UserPermission implements Serializable {

    /// 編號
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_")
    private Long id;

    /// 權限代碼
    @Column(name = "CODE_", nullable = false, unique = true)
    private String code;

    /// 權限名稱
    @Column(name = "NAME_", nullable = false)
    private String name;

    /// 權限類型
    @Column(name = "TYPE_")
    @Enumerated(EnumType.STRING)
    private PermissionType type;

    /// 權限描述
    @Column(name = "DESCRIPTION_")
    private String description;
}
