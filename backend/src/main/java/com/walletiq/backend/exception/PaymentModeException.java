package com.walletiq.backend.exception;

import com.walletiq.backend.enums.ErrorCode;

public class PaymentModeException extends BaseException {

    public PaymentModeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PaymentModeException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}