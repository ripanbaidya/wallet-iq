package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

public class UserException extends BaseException {

    public UserException(ErrorCode code) {
        super(code);
    }

    public UserException(ErrorCode code, String message) {
        super(code, message);
    }
}
