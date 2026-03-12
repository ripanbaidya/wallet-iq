package com.walletiq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorType {

    VALIDATION("Validation Error", HttpStatus.BAD_REQUEST.value()), // 400
    AUTHENTICATION("Authentication Error", HttpStatus.UNAUTHORIZED.value()), // 401
    AUTHORIZATION("Authorization Error", HttpStatus.FORBIDDEN.value()), // 403
    NOT_FOUND("Resource Not Found", HttpStatus.NOT_FOUND.value()), // 404
    CONFLICT("Conflict", HttpStatus.CONFLICT.value()), // 409
    BUSINESS("Business Logic Error", HttpStatus.UNPROCESSABLE_ENTITY.value()), // 422
    INTERNAL("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()), // 500
    SERVICE_UNAVAILABLE("Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE.value()) // 503
    ;

    private final String title;
    private final int statusCode;
}
