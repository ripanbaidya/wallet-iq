package com.walletiq.backend.exception;

import com.walletiq.backend.enums.ErrorCode;

public class VectorSearchException extends BaseException {

    public VectorSearchException(ErrorCode errorCode) {
        super(errorCode);
    }

    public VectorSearchException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public VectorSearchException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(errorCode, customMessage, cause);
    }
}