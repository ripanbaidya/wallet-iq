package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

/**
 * Exception thrown for transaction related failures.
 * <p>This exception occurs when financial transaction operations fail,
 * such as creating, updating, retrieving, or validating transaction data.
 */
public class TransactionException extends BaseException {

    public TransactionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TransactionException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}