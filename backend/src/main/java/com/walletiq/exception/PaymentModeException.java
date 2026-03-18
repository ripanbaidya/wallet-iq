package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

/**
 * Exception thrown for payment mode related failures.
 * <p>This exception occurs when operations involving payment modes fail,
 * such as creating, updating, retrieving, or validating supported payment
 * methods.
 */
public class PaymentModeException extends BaseException {

    public PaymentModeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PaymentModeException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}