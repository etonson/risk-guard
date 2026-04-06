package com.applications.common.dto;

import lombok.Getter;

/**
 * API Result Codes
 *
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "請求參數錯誤"),
    UNAUTHORIZED(401, "尚未登入或 Token 已過期"),
    FORBIDDEN(403, "權限不足"),
    NOT_FOUND(404, "資源不存在"),
    INTERNAL_ERROR(500, "伺服器內部錯誤"),
    BIZ_ERROR(1000, "業務邏輯錯誤");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
