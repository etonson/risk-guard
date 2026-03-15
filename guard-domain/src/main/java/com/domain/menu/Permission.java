package com.domain.menu;

import lombok.Builder;
import lombok.Data;

/**
 * Permission Domain Entity
 * 
 * Represents a permission in the system
 * 
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Data
@Builder
public class Permission {
    private String id;
    private String name;
    private String description;
}

