package com.applications.auth;

public record ApiResponse<T>(
        boolean success,
        T data,
        String message
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "ok");
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
