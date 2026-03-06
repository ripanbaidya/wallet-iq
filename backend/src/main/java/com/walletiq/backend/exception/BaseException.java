package com.walletiq.backend.exception;

import com.walletiq.backend.enums.ErrorCode;
import lombok.Getter;

/**
 * Base exception class for all custom exceptions
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String customMessage;

    protected BaseException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    protected BaseException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    protected BaseException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    public String getResolvedMessage() {
        return customMessage == null ? errorCode.getDefaultMessage()
            : customMessage;
    }
}
