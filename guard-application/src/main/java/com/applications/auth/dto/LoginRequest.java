package com.applications.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Login Request
 */
public record LoginRequest(
    String username,
    String email,
    @NotBlank(message = "密碼不能為空")
    String password
) {}
