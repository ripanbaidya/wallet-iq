package com.walletiq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Defines standardized error codes categorized by specific error types with
 * corresponding default messages.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // =========================
    // Authentication & Security
    // =========================
    INVALID_CREDENTIALS(ErrorType.AUTHENTICATION, "The provided email or password is incorrect"),
    UNAUTHENTICATED(ErrorType.AUTHENTICATION, "Authentication is required to access this resource"),
    ACCESS_DENIED(ErrorType.AUTHORIZATION, "You do not have permission to access this resource"),
    ACCOUNT_DISABLED(ErrorType.AUTHORIZATION, "This account has been disabled and cannot be used to log in"),

    // JWT
    TOKEN_EXPIRED(ErrorType.AUTHENTICATION, "The authentication token has expired"),
    TOKEN_INVALID(ErrorType.AUTHENTICATION, "The authentication token is invalid or malformed"),
    TOKEN_MISSING_CLAIM(ErrorType.AUTHENTICATION, "The authentication token is missing a required claim"),
    TOKEN_SIGNATURE_INVALID(ErrorType.AUTHENTICATION, "Token signature verification failed"),
    TOKEN_UNSUPPORTED(ErrorType.AUTHENTICATION, "The provided token format is not supported"),

    // Refresh Token
    REFRESH_TOKEN_NOT_FOUND(ErrorType.AUTHENTICATION, "The refresh token was not found"),
    REFRESH_TOKEN_EXPIRED(ErrorType.AUTHENTICATION, "The refresh token has expired"),
    REFRESH_TOKEN_REVOKED(ErrorType.AUTHENTICATION, "The refresh token has been revoked and cannot be used"),


    // =========================
    // User
    // =========================
    USER_NOT_FOUND(ErrorType.NOT_FOUND, "The requested user was not found"),
    USER_ALREADY_EXISTS(ErrorType.CONFLICT, "A user with this email address already exists"),
    EMAIL_ALREADY_EXISTS(ErrorType.CONFLICT, "An account with this email address already exists"),


    // =========================
    // OTP Verification
    // =========================
    OTP_INVALID(ErrorType.VALIDATION, "The provided OTP is invalid or has already been used"),
    OTP_EXPIRED(ErrorType.VALIDATION, "The OTP has expired. Please request a new verification code"),
    OTP_SEND_FAILED(ErrorType.INTERNAL, "Failed to send the verification email. Please try again later"),
    EMAIL_ALREADY_VERIFIED(ErrorType.VALIDATION, "The email address has already been verified"),


    // =========================
    // Category
    // =========================
    CATEGORY_NOT_FOUND(ErrorType.NOT_FOUND, "The requested category was not found"),
    CATEGORY_ALREADY_EXISTS(ErrorType.CONFLICT, "A category with the same name already exists"),
    CATEGORY_ACCESS_DENIED(ErrorType.AUTHORIZATION, "You do not have permission to modify this category"),
    INVALID_CATEGORY_TYPE(ErrorType.VALIDATION, "The provided category type is invalid"),


    // =========================
    // Payment Mode
    // =========================
    PAYMENT_MODE_NOT_FOUND(ErrorType.NOT_FOUND, "The requested payment mode was not found"),
    PAYMENT_MODE_ALREADY_EXISTS(ErrorType.CONFLICT, "A payment mode with the same name already exists"),


    // =========================
    // Transaction
    // =========================
    TRANSACTION_NOT_FOUND(ErrorType.NOT_FOUND, "The requested transaction was not found"),
    TRANSACTION_ACCESS_DENIED(ErrorType.AUTHORIZATION, "You do not have permission to modify this transaction"),
    INVALID_TRANSACTION(ErrorType.VALIDATION, "The provided transaction data is invalid or incomplete"),


    // =========================
    // Recurring Transaction
    // =========================
    RECURRING_NOT_FOUND(ErrorType.NOT_FOUND, "The requested recurring transaction was not found"),
    RECURRING_ALREADY_INACTIVE(ErrorType.VALIDATION, "The recurring transaction is already inactive"),
    RECURRING_END_DATE_BEFORE_START(ErrorType.VALIDATION, "The end date must be after the start date"),
    RECURRING_INVALID_FREQUENCY(ErrorType.VALIDATION, "The provided recurring frequency is invalid"),
    RECURRING_ACCESS_DENIED(ErrorType.AUTHORIZATION, "You do not have permission to modify this recurring transaction"),


    // =========================
    // Savings Goal
    // =========================
    GOAL_NOT_FOUND(ErrorType.NOT_FOUND, "The requested savings goal was not found"),
    GOAL_ACCESS_DENIED(ErrorType.AUTHORIZATION, "You do not have permission to access this savings goal"),
    GOAL_ALREADY_ACHIEVED(ErrorType.VALIDATION, "This savings goal has already been achieved"),
    GOAL_ALREADY_FAILED(ErrorType.VALIDATION, "This savings goal has already failed because the deadline has passed"),
    GOAL_INVALID_CONTRIBUTION(ErrorType.VALIDATION, "The contribution amount must be greater than zero"),
    GOAL_DEADLINE_PAST(ErrorType.VALIDATION, "The goal deadline must be set to a future date"),


    // =========================
    // Budget
    // =========================
    BUDGET_NOT_FOUND(ErrorType.NOT_FOUND, "The requested budget was not found"),
    BUDGET_ACCESS_DENIED(ErrorType.AUTHORIZATION, "You do not have permission to access this budget"),
    BUDGET_DUPLICATE(ErrorType.CONFLICT, "A budget already exists for the selected category and month"),
    BUDGET_INVALID_MONTH(ErrorType.VALIDATION, "Invalid month format. Please use yyyy-MM"),
    BUDGET_INVALID_THRESHOLD(ErrorType.VALIDATION, "The alert threshold must be between 1 and 100"),


    // =========================
    // Notification
    // =========================
    NOTIFICATION_NOT_FOUND(ErrorType.NOT_FOUND, "The requested notification was not found"),


    // =========================
    // Chat Session
    // =========================
    CHAT_SESSION_NOT_FOUND(ErrorType.NOT_FOUND, "The requested chat session was not found"),


    // =========================
    // RAG / AI
    // =========================
    RAG_QUERY_FAILED(ErrorType.INTERNAL, "Failed to process the AI query"),
    RAG_NO_CONTEXT(ErrorType.NOT_FOUND, "No relevant transaction data was found to answer the question"),
    RAG_PROMPT_LOAD_FAILED(ErrorType.INTERNAL, "Failed to load the AI system configuration"),
    VECTOR_SEARCH_FAILED(ErrorType.INTERNAL, "The vector search operation failed"),


    // =========================
    // CSV
    // =========================
    CSV_EXPORT_FAILED(ErrorType.INTERNAL, "Failed to generate the CSV export file"),


    // =========================
    // Key Loading / Security Infrastructure
    // =========================
    KEY_FILE_NOT_FOUND(ErrorType.INTERNAL, "The key file could not be found"),
    KEY_FILE_NOT_READABLE(ErrorType.INTERNAL, "The key file exists but cannot be read"),
    KEY_FILE_READ_FAILED(ErrorType.INTERNAL, "Failed to read the key file"),
    INVALID_KEY_FORMAT(ErrorType.INTERNAL, "The key file format or encoding is invalid"),
    PRIVATE_KEY_LOAD_FAILED(ErrorType.INTERNAL, "Failed to load the private key"),
    PUBLIC_KEY_LOAD_FAILED(ErrorType.INTERNAL, "Failed to load the public key"),


    // =========================
    // General
    // =========================
    VALIDATION_FAILED(ErrorType.VALIDATION, "One or more request fields are invalid"),
    INTERNAL_ERROR(ErrorType.INTERNAL, "An unexpected internal server error occurred"),
    SERVICE_UNAVAILABLE(ErrorType.SERVICE_UNAVAILABLE, "The service is temporarily unavailable. Please try again later");


    private final ErrorType type;
    private final String defaultMessage;
}