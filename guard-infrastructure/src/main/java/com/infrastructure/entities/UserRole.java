package com.infrastructure.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * User Role Mapping Entity
 * <p>
 * Maps users to their assigned roles
 * 
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER_ROLE_", uniqueConstraints = @UniqueConstraint(columnNames = {"USER_ID_", "ROLE_ID_"}))
public class UserRole {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_")
    private Long id;
    
    @Column(name = "USER_ID_", nullable = false)
    private Long userId;
    
    @Column(name = "ROLE_ID_", nullable = false)
    private String roleId;
    
    @Column(name = "ROLE_NAME_", nullable = false)
    private String roleName;
    
    @Column(name = "CREATED_AT_", updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

