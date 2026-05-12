package online.walletiq.exception;

import online.walletiq.enums.ErrorCode;

/**
 * Exception thrown for email related failures.
 * <p>This exception represents errors that occur during email operations,
 * such as sending emails, email template processing, or email delivery failures.
 */
public class EmailException extends BusinessException {

    public EmailException(ErrorCode errorCode) {
        super(errorCode);
    }

    public EmailException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
