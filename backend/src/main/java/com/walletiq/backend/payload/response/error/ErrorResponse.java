package com.walletiq.backend.payload.response.error;

public record ErrorResponse(
    boolean success,
    ErrorDetail error
) {

    public static ErrorResponse of(ErrorDetail error) {
        return new ErrorResponse(false, error);
    }
}