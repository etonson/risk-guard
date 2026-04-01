package com.infrastructure.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * User Permission Mapping Entity
 * <p>
 * Maps users to their assigned permissions
 * 
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER_PERMISSION_", uniqueConstraints = @UniqueConstraint(columnNames = {"USER_ID_", "PERMISSION_ID_"}))
public class UserPermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_")
    private Long id;
    
    @Column(name = "USER_ID_", nullable = false)
    private Long userId;
    
    @Column(name = "PERMISSION_ID_", nullable = false)
    private String permissionId;
    
    @Column(name = "PERMISSION_NAME_", nullable = false)
    private String permissionName;
    
    @Column(name = "CREATED_AT_", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

