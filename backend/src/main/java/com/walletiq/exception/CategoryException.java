package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

public class CategoryException extends BaseException {

    public CategoryException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CategoryException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}