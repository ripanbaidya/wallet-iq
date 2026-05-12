package online.walletiq.exception;

import online.walletiq.enums.ErrorCode;

/**
 * Exception thrown for subscription-related failures.
 * <p>Covers scenarios such as plan not found, payment verification failure,
 * or attempts to access features that require an active subscription.
 */
public class SubscriptionException extends BusinessException {

    public SubscriptionException(ErrorCode errorCode) {
        super(errorCode);
    }
}