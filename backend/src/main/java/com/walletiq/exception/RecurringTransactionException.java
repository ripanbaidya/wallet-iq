package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

/**
 * Exception thrown for recurring transaction related failures.
 * <p>This exception occurs when operations involving recurring transactions
 * fail, such as scheduling, updating, executing, or cancelling recurring
 * payments.
 */
public class RecurringTransactionException extends BaseException {

    public RecurringTransactionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RecurringTransactionException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
