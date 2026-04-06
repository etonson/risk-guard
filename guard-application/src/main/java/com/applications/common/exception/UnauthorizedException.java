package com.applications.common.exception;

import com.applications.common.dto.ResultCode;
import lombok.Getter;

/**
 * Unauthorized Exception
 *
 * @Author: Eton.Lin
 * @Date: 2026/3/15
 */
@Getter
public class UnauthorizedException extends RuntimeException {
    private final ResultCode resultCode = ResultCode.UNAUTHORIZED;

    public UnauthorizedException(String message) {
        super(message);
    }
}
