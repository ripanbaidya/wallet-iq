package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

/**
 * Exception thrown for notification related failures.
 * <p>This exception represents errors that occur during notification processing,
 * such as sending alerts, generating notifications, or handling notification
 * delivery mechanisms.
 */
public class NotificationException extends BaseException {

    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
