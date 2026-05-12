package online.walletiq.exception.handler;

import online.walletiq.enums.ErrorCode;
import online.walletiq.exception.BusinessException;
import online.walletiq.dto.error.ErrorDetail;
import online.walletiq.dto.error.ErrorResponse;
import online.walletiq.dto.error.FieldError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Global exception handler for the entire application.
 * This class centralizes exception handling for all REST controllers using
 * {@link RestControllerAdvice}. It converts different types of exceptions into
 * a consistent {@link ErrorResponse} format that the API returns to clients.
 * <p>Responsibilities are includes: Handling custom business exceptions, Handling
 * validation errors, Handling request parameter mismatches, Handling database access
 * failures, Catch any unexpected exceptions and so on.
 * <br>All responses follow the standardized API error format using
 * {@link ErrorResponse} and {@link ErrorDetail}.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles all custom application exceptions extending {@link BusinessException}.
     * <br>These exceptions represent known business errors such as:
     * Authentication failures, Authorization issues, Domain validation errors,
     * Resource not found etc.
     * <p>The HTTP status is derived from the associated {@link ErrorCode}.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BusinessException ex,
                                                             HttpServletRequest request) {

        // Log known application exception at WARN level
        log.warn("Base exception: [{}] - {}", ex.getErrorCode(), ex.getResolvedMessage());

        // Build standardized API error response
        var errorResponse = ErrorResponse.of(
            ErrorDetail.builder()
                .code(ex.getErrorCode())
                .path(request.getRequestURI())
                .build()
        );

        return ResponseEntity
            .status(ex.getErrorCode().getType().getStatusCode())
            .body(errorResponse);
    }

    /**
     * Handles validation failures for request bodies annotated with {@code @Valid}.
     * <p>Triggered when DTO validation fails during request body binding.
     * Example:
     * <pre>
     * POST /users
     * {
     *   "email": "",
     *   "password": "123"
     * }
     * </pre>
     * </p>
     * <p>All field-level validation errors are collected and returned in the
     * {@code errors} array of the API response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                                                                   HttpServletRequest request) {

        // Extract field validation errors
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors()
            .stream()
            .map(error -> new FieldError(error.getField(), error.getDefaultMessage()))
            .toList();

        // Build API error response with field-level details
        var errorResponse = ErrorResponse.of(
            ErrorDetail.builder()
                .code(ErrorCode.VALIDATION_FAILED)
                .path(request.getRequestURI())
                .errors(fieldErrors)
                .build());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Handles validation failures for query parameters and path variables.
     * <p>Triggered when constraints defined on controller method parameters fail.
     * Example:</p>
     * <pre>
     * GET /users?age=-1
     * </pre>
     * <p>Converts constraint violations into structured field errors.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {

        // Convert constraint violations to API field error objects
        List<FieldError> fieldErrors = ex.getConstraintViolations()
            .stream()
            .map(cv -> {
                String field = cv.getPropertyPath().toString();

                // Strip method prefix (e.g. "createUser.email" → "email")
                if (field.contains(".")) {
                    field = field.substring(field.lastIndexOf('.') + 1);
                }

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

    /**
     * Handles illegal argument or illegal state errors thrown within the application.
     * <p>These exceptions typically occur when: Invalid method arguments are passed,
     * Business rules are violated, An operation is executed in an invalid state etc.
     * <p>The error message is returned directly to the client.
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleIllegalArgument(RuntimeException ex,
                                                               HttpServletRequest request) {

        log.warn("Illegal argument/state at {}: {}", request.getRequestURI(), ex.getMessage());

        var response = ErrorResponse.of(
            ErrorDetail.builder()
                .code(ErrorCode.VALIDATION_FAILED)
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build()
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles type mismatch errors for request parameters or path variables.
     * <p>Example:
     * <pre>
     * GET /transactions/{id}
     * id = "abc" (expected UUID)
     * </pre>
     * <p>Returns a clear message indicating the expected parameter type.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {

        String expectedType = ex.getRequiredType() != null
            ? ex.getRequiredType().getSimpleName()
            : "unknown";

        String detail = String.format("Parameter '%s' must be of type %s", ex.getName(), expectedType);

        log.warn("Type mismatch at {}: {}", request.getRequestURI(), detail);

        var response = ErrorResponse.of(
            ErrorDetail.builder()
                .code(ErrorCode.VALIDATION_FAILED)
                .message(detail)
                .path(request.getRequestURI())
                .build()
        );

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handles database access failures.
     * <p>Examples include: Constraint violations, Query execution errors, Connection
     * failures
     * <p>The root cause message is logged for debugging while returning a standardized
     * internal server error response.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccess(DataAccessException ex,
                                                          HttpServletRequest request) {

        String rootCause = ex.getMostSpecificCause().getMessage();

        log.error("Data access failure at {}: {}", request.getRequestURI(), rootCause, ex);

        var response = ErrorResponse.of(
            ErrorDetail.builder()
                .code(ErrorCode.INTERNAL_ERROR)
                .message(rootCause)
                .path(request.getRequestURI())
                .build()
        );

        return ResponseEntity.internalServerError().body(response);
    }

    /**
     * Fallback handler for all unhandled exceptions.
     * <p>This ensures the API never exposes stack traces or internal implementation
     * details to the client.
     * <p>All unexpected failures are logged at ERROR level for investigation.
     */
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