package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

public class CsvException extends BaseException {

    public CsvException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CsvException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public CsvException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}