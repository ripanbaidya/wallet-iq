package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

/**
 * Exception thrown for budget related failures.
 * <p>This exception represents errors that occur during budget operations,
 * such as creating, updating, retrieving, or enforcing budget constraints.
 */
public class BudgetException extends BaseException {

    public BudgetException(ErrorCode errorCode) {
        super(errorCode);
    }
}
