package com.config;

import com.applications.common.dto.ApiResponse;
import com.applications.common.dto.ResultCode;
import com.applications.common.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global Exception Handler
 * 將領域/應用層拋出的例外，統一包裝為 ApiResponse
 *
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleUnauthorizedException(UnauthorizedException e) {
        log.warn("認證失敗: {}", e.getMessage());
        return ApiResponse.error(e.getResultCode(), e.getMessage());
    }

    /**
     * 處理 Spring Security 認證異常
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<Void> handleAuthenticationException(AuthenticationException e) {
        String message = "認證失敗";
        if (e instanceof DisabledException) {
            message = "帳號已被停用，請聯繫管理員";
        } else if (e instanceof BadCredentialsException) {
            message = "帳號或密碼錯誤";
        }
        log.warn("Security 認證異常: {} - {}", e.getClass().getSimpleName(), e.getMessage());
        return ApiResponse.error(ResultCode.UNAUTHORIZED, message);
    }

    /**
     * 處理 Bean Validation 失敗 (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("參數校驗失敗: {}", errorMessage);
        return ApiResponse.error(ResultCode.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGenericException(Exception e) {
        log.error("未預期的錯誤", e);
        return ApiResponse.error(ResultCode.INTERNAL_ERROR, "系統繁忙，請稍後再試");
    }
}
