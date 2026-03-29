package com.applications.common.exception;

import com.applications.common.dto.ResultCode;
import lombok.Getter;

/**
 * Unauthorized Exception
 */
@Getter
public class UnauthorizedException extends RuntimeException {
    private final ResultCode resultCode = ResultCode.UNAUTHORIZED;

    public UnauthorizedException(String message) {
        super(message);
    }
}
