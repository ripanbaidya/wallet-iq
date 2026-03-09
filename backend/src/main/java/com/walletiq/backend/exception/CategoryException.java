package com.walletiq.backend.exception;

import com.walletiq.backend.enums.ErrorCode;

public class CategoryException extends BaseException {

    public CategoryException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CategoryException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}