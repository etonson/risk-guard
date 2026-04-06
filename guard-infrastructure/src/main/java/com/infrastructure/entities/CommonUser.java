package com.infrastructure.entities;

import com.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 使用者實體
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "COMMON_USER_")
public class CommonUser implements Serializable {

    /// 編號
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_")
    private Long id;

    /// 使用者名稱
    @NotBlank(message = "使用者名稱不能為空")
    @Column(name = "USER_NAME_", nullable = false, unique = true)
    private String username;

    /// 電子郵箱
    @Email(message = "電子郵箱格式不正確")
    @Column(name = "EMAIL_", nullable = false, unique = true)
    private String email;

    /// 密碼
    @NotBlank(message = "密碼不能為空")
    @Column(name="PASSWORD_",nullable = false)
    private String password;

    /// 真實姓名
    @Column(name = "REAL_NAME_")
    private String realName;

    /// 狀態 (預設為正常)
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private UserStatus status = UserStatus.ACTIVE;

    /// 角色集合
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "SYS_USER_ROLE_",
            joinColumns = @JoinColumn(name = "USER_ID_"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID_")
    )
    private Set<UserRole> roles;

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
