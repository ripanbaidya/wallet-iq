package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

public class KeyLoadException extends BaseException {

    public KeyLoadException(ErrorCode errorCode) {
        super(errorCode);
    }

    public KeyLoadException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public KeyLoadException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
