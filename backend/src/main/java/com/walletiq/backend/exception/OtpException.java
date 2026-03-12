package com.walletiq.backend.exception;

import com.walletiq.backend.enums.ErrorCode;

public class OtpException extends BaseException {

    public OtpException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OtpException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
