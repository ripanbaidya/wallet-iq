package online.walletiq.exception;

import online.walletiq.enums.ErrorCode;

/**
 * Exception thrown for category related failures.
 * <p>This exception is used when operations involving transaction categories
 * fail, such as creating, updating, deleting, or retrieving categories.
 */
public class CategoryException extends BusinessException {

    public CategoryException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CategoryException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}