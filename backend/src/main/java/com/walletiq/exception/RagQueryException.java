package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

public class RagQueryException extends BaseException {

    public RagQueryException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RagQueryException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public RagQueryException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}