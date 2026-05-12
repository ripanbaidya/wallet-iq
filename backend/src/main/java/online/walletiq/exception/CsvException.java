package online.walletiq.exception;

import online.walletiq.enums.ErrorCode;

/**
 * Exception thrown for CSV processing related failures.
 * <p>This exception represents errors that occur during CSV operations,
 * such as importing, exporting, parsing, or validating CSV data.
 */
public class CsvException extends BusinessException {

    public CsvException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CsvException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public CsvException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}