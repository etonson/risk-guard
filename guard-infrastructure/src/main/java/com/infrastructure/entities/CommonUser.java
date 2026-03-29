package com.infrastructure.entities;

import com.domain.user.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "COMMON_USER_")
public class CommonUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_")
    private Long id; // 編號

    @NotBlank(message = "使用者名稱不能為空")
    @Column(name = "USER_NAME_", nullable = false, unique = true)
    private String username; // 使用者名稱

    @Email(message = "電子郵箱格式不正確")
    @Column(name = "EMAIL_", nullable = false, unique = true)
    private String email; // 電子郵箱

    @NotBlank(message = "密碼不能為空")
    @Column(name="PASSWORD_",nullable = false)
    private String password; // 密碼

    @Column(name = "REAL_NAME_")
    private String realName; // 真實姓名

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private UserStatus status = UserStatus.ACTIVE;  // 預設為正常

    @Column(name = "CREATED_AT_", updatable = false)
    private LocalDateTime createdAt; // 建立時間

    @Column(name = "UPDATED_AT_")
    private LocalDateTime updatedAt; // 更新時間

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
