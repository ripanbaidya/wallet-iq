package online.walletiq.exception;

import online.walletiq.enums.ErrorCode;

/**
 * Exception thrown for cryptographic key loading failures.
 * <p>This exception occurs when the application fails to load or initialize
 * security keys required for cryptographic operations such as JWT signing
 * or verification.
 */
public class KeyLoadException extends BusinessException {

    public KeyLoadException(ErrorCode errorCode) {
        super(errorCode);
    }

    public KeyLoadException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public KeyLoadException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
