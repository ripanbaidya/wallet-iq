package com.walletiq.exception.handler;

import com.walletiq.enums.ErrorCode;
import com.walletiq.exception.BaseException;
import com.walletiq.dto.error.ErrorDetail;
import com.walletiq.dto.error.ErrorResponse;
import com.walletiq.dto.error.FieldError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Handle base exception
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex,
                                                             HttpServletRequest request) {
        // Log the exception
        log.warn("Base exception: [{}] - {}", ex.getErrorCode(), ex.getResolvedMessage());
        // Build the error response
        var errorResponse = ErrorResponse.of(
            ErrorDetail.builder()
                .code(ex.getErrorCode())
                .path(request.getRequestURI())
                .build()
        );
        return ResponseEntity.status(ex.getErrorCode().getType().getStatusCode()).body(errorResponse);
    }

    // Handle validation exception (@Valid/ @Validated)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                                                                   HttpServletRequest request) {
        // Collect field details
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
            .toList();

        var errorResponse = ErrorResponse.of(
            ErrorDetail.builder()
                .code(ErrorCode.VALIDATION_FAILED)
                .path(request.getRequestURI())
                .errors(fieldErrors)
                .build());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    // Handle constraint violation exception (query params, path variable)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {
        // Collect field error details
        List<FieldError> fieldErrors = ex.getConstraintViolations()
            .stream()
            .map(cv -> {
                String field = cv.getPropertyPath().toString();
                // Strip method prefix: "createUser.email" → "email"
                if (field.contains(".")) field = field.substring(field.lastIndexOf('.') + 1);
                return new FieldError(field, cv.getMessage());
            })
            .toList();

        var response = ErrorResponse.of(
            ErrorDetail.builder()
                .code(ErrorCode.VALIDATION_FAILED)
                .path(request.getRequestURI())
                .errors(fieldErrors)
                .build()
        );

        return ResponseEntity.badRequest().body(response);
    }

    // Unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex,
                                                          HttpServletRequest request) {
        log.error("Unhandled exception at {}", request.getRequestURI(), ex);
        var response = ErrorResponse.of(
            ErrorDetail.builder()
                .code(ErrorCode.INTERNAL_ERROR)
                .path(request.getRequestURI())
                .build()
        );

        return ResponseEntity.internalServerError().body(response);
    }
}
