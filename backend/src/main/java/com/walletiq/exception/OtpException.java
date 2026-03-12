package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

public class OtpException extends BaseException {

    public OtpException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OtpException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
