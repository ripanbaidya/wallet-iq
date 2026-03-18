package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

/**
 * Exception thrown for JWT authentication related failures.
 * <p>This exception is used when JWT processing fails, such as token validation,
 * parsing errors, expired tokens, or invalid signatures.
 */
public class JwtAuthenticationException extends BaseException {

    public JwtAuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public JwtAuthenticationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}