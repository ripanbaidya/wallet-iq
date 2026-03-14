package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

public class SavingsGoalException extends BaseException {

    public SavingsGoalException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SavingsGoalException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
