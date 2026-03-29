package config;


import com.applications.common.dto.ApiResponse;
import com.applications.common.dto.ResultCode;
import com.applications.common.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global Exception Handler
 * 將領域/應用層拋出的例外，統一包裝為 ApiResponse
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ApiResponse<Void> handleUnauthorizedException(UnauthorizedException e) {
        log.warn("認證失敗: {}", e.getMessage());
        return ApiResponse.error(e.getResultCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleGenericException(Exception e) {
        log.error("未預期的錯誤", e);
        return ApiResponse.error(ResultCode.INTERNAL_ERROR, "系統繁忙，請稍後再試");
    }
}
