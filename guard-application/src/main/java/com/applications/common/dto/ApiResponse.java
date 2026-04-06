package com.applications.common.dto;

import java.time.Instant;

/**
 * Standard API Response Structure
 *
 * @param <T> Type of data
 *
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
public record ApiResponse<T>(
        int code,
        String message,
        T data,
        long timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                ResultCode.SUCCESS.getCode(),
                ResultCode.SUCCESS.getMessage(),
                data,
                Instant.now().toEpochMilli()
        );
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(
                ResultCode.SUCCESS.getCode(),
                message,
                data,
                Instant.now().toEpochMilli()
        );
    }

    public static <T> ApiResponse<T> error(ResultCode resultCode) {
        return new ApiResponse<>(
                resultCode.getCode(),
                resultCode.getMessage(),
                null,
                Instant.now().toEpochMilli()
        );
    }

    public static <T> ApiResponse<T> error(ResultCode resultCode, String message) {
        return new ApiResponse<>(
                resultCode.getCode(),
                message,
                null,
                Instant.now().toEpochMilli()
        );
    }
    
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(
                code,
                message,
                null,
                Instant.now().toEpochMilli()
        );
    }
}
