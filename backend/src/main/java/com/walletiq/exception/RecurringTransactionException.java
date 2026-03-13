package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

public class RecurringTransactionException extends BaseException {

    public RecurringTransactionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RecurringTransactionException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
