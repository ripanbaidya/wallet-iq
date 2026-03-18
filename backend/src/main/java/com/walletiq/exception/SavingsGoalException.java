package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

/**
 * Exception thrown for savings goal related failures.
 * <p>This exception represents errors that occur during savings goal
 * operations such as creating goals, contributing funds, tracking progress,
 * or managing goal status.
 */
public class SavingsGoalException extends BaseException {

    public SavingsGoalException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SavingsGoalException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
