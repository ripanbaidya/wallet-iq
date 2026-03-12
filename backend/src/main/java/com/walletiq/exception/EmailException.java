package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

public class EmailException extends BaseException {

    public EmailException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EmailException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
