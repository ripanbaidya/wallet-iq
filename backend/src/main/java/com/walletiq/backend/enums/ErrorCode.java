package com.walletiq.backend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Defines standardized error codes categorized by specific error types with
 * corresponding default messages.
 * Each error code is associated with a particular business entity (e.g., user,
 * wallet, transaction) to provide clear, consistent error handling across the
 * application.
 *
 * @see ErrorType
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // User
    USER_NOT_FOUND(ErrorType.NOT_FOUND, "User not found"),
    USER_ALREADY_EXISTS(ErrorType.CONFLICT, "A user with this email already exists"),

    // Key loading
    KEY_FILE_NOT_FOUND(ErrorType.INTERNAL, "Key file not found"),
    KEY_FILE_NOT_READABLE(ErrorType.INTERNAL, "Key file is not readable"),
    KEY_FILE_READ_FAILED(ErrorType.INTERNAL, "Failed to read key file"),
    INVALID_KEY_FORMAT(ErrorType.INTERNAL, "Invalid key format or encoding"),
    PRIVATE_KEY_LOAD_FAILED(ErrorType.INTERNAL, "Failed to load private key"),
    PUBLIC_KEY_LOAD_FAILED(ErrorType.INTERNAL, "Failed to load public key"),

    // Jwt
    TOKEN_EXPIRED(ErrorType.AUTHENTICATION, "Token has expired"),
    TOKEN_INVALID(ErrorType.AUTHENTICATION, "Token is invalid or malformed"),
    TOKEN_MISSING_CLAIM(ErrorType.AUTHENTICATION, "Token is missing a required claim"),
    TOKEN_SIGNATURE_INVALID(ErrorType.AUTHENTICATION, "Token signature verification failed"),
    TOKEN_UNSUPPORTED(ErrorType.AUTHENTICATION, "Token format is not supported"),

    // Auth domain
    INVALID_CREDENTIALS(ErrorType.AUTHENTICATION, "Invalid email or password"),
    EMAIL_ALREADY_EXISTS(ErrorType.CONFLICT, "An account with this email already exists"),
    ACCOUNT_DISABLED(ErrorType.AUTHORIZATION, "This account has been disabled"),

    // Refresh token
    REFRESH_TOKEN_NOT_FOUND(ErrorType.AUTHENTICATION, "Refresh token not found"),
    REFRESH_TOKEN_EXPIRED(ErrorType.AUTHENTICATION, "Refresh token has expired"),
    REFRESH_TOKEN_REVOKED(ErrorType.AUTHENTICATION, "Refresh token has been revoked"),

    UNAUTHENTICATED(ErrorType.AUTHENTICATION, "Authentication is required to access this resource"),
    ACCESS_DENIED(ErrorType.AUTHORIZATION, "You do not have permission to access this resource"),

    // Category
    CATEGORY_NOT_FOUND(ErrorType.NOT_FOUND, "Category not found"),
    CATEGORY_ALREADY_EXISTS(ErrorType.CONFLICT, "A category with this name already exists"),
    CATEGORY_ACCESS_DENIED(ErrorType.AUTHORIZATION, "You do not have permission to modify this category"),

    // PaymentMode
    PAYMENT_MODE_NOT_FOUND(ErrorType.NOT_FOUND, "Payment mode not found"),
    PAYMENT_MODE_ALREADY_EXISTS(ErrorType.CONFLICT, "A payment mode with this name already exists"),

    // Transaction
    TRANSACTION_NOT_FOUND(ErrorType.NOT_FOUND, "Transaction not found"),
    TRANSACTION_ACCESS_DENIED(ErrorType.AUTHORIZATION, "You do not have permission to modify this transaction"),
    INVALID_TRANSACTION(ErrorType.VALIDATION, "Transaction data is invalid"),

    // Chat Session
    CHAT_SESSION_NOT_FOUND(ErrorType.NOT_FOUND, "Chat session not found"),

    // General
    VALIDATION_FAILED(ErrorType.VALIDATION, "One or more fields are invalid"),
    INTERNAL_ERROR(ErrorType.INTERNAL, "An unexpected error occurred"),
    SERVICE_UNAVAILABLE(ErrorType.SERVICE_UNAVAILABLE, "The service is temporarily unavailable");

    private final ErrorType type;
    private final String defaultMessage;
}

