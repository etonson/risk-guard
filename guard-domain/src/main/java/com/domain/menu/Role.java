package com.domain.menu;

import lombok.Builder;
import lombok.Data;

/**
 * Role Domain Entity
 * 
 * Represents a role in the system
 * 
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Data
@Builder
public class Role {
    private String id;
    private String name;
    private String description;
}

