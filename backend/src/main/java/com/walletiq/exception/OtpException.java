package com.walletiq.exception;

import com.walletiq.enums.ErrorCode;

/**
 * Exception thrown for OTP related failures.
 * <p>This exception is used when operations involving one-time passwords fail,
 * such as OTP generation, validation, expiration handling, or verification errors.
 */
public class OtpException extends BaseException {

    public OtpException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OtpException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
