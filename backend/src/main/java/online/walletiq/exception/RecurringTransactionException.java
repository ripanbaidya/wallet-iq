package online.walletiq.exception;

import online.walletiq.enums.ErrorCode;

/**
 * Exception thrown for recurring transaction related failures.
 * <p>This exception occurs when operations involving recurring transactions
 * fail, such as scheduling, updating, executing, or cancelling recurring
 * payments.
 */
public class RecurringTransactionException extends BusinessException {

    public RecurringTransactionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RecurringTransactionException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}
