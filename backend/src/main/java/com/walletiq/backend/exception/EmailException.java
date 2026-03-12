package com.walletiq.backend.exception;

import com.walletiq.backend.enums.ErrorCode;

public class EmailException extends BaseException {

    public EmailException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EmailException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
