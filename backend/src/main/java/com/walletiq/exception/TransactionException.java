package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

public class TransactionException extends BaseException {

    public TransactionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TransactionException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}