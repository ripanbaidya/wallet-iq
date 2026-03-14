package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

public class BudgetException extends BaseException {

    public BudgetException(ErrorCode errorCode) {
        super(errorCode);
    }
}
