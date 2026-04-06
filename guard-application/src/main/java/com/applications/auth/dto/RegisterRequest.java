package com.applications.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Register Request
 */
public record RegisterRequest(
    @NotBlank(message = "電子郵件不能為空")
    @Email(message = "電子郵件格式不正確")
    String email,

    @NotBlank(message = "用戶名稱不能為空")
    @Size(min = 3, max = 20, message = "用戶名稱長度需在 3 到 20 之間")
    String username,

    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, message = "密碼長度至少需 6 個字元")
    String password
) {}
