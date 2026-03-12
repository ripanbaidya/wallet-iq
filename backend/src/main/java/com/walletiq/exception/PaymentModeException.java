package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

public class PaymentModeException extends BaseException {

    public PaymentModeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PaymentModeException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}