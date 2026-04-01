package com.applications.auth.dto;

import java.util.List;

/**
 *
 *
 * @Author: Eton.Lin
 * @Date: 2026/4/1 下午8:45
 */
public record UserInfo(
        Long id,
        String email,
        String name,
        List<String> roles
) {}