package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

public class JwtAuthenticationException extends BaseException {

    public JwtAuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public JwtAuthenticationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public JwtAuthenticationException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}