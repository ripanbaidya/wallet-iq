package online.walletiq.exception;

import online.walletiq.enums.ErrorCode;

/**
 * Exception thrown for user related failures.
 * <p>This exception represents errors that occur during user operations,
 * such as account creation, profile updates, authentication checks, or
 * user validation processes.
 */
public class UserException extends BusinessException {

    public UserException(ErrorCode code) {
        super(code);
    }

    public UserException(ErrorCode code, String message) {
        super(code, message);
    }
}
