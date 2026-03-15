package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

public class NotificationException extends BaseException {

    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
