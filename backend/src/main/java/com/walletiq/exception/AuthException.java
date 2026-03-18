package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

/**
 * Exception thrown for authentication related failures.
 * <p>This exception is used when authentication operations fail,
 * such as invalid credentials, expired tokens, or unauthorized access attempts.
 */
public class AuthException extends BaseException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}