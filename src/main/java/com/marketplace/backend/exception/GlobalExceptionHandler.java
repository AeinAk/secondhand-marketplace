package com.marketplace.backend.exception;

import com.marketplace.backend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler for the marketplace application.
 * <p>
 * This class intercepts exceptions thrown by controllers and converts them into
 * standardized API responses using the {@link ApiResponse} format. It handles
 * various exception types including business logic violations, authentication
 * failures, access denials, validation errors, and generic unexpected errors.
 * This ensures consistent error responses across all API endpoints.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles business logic exceptions.
     * <p>
     * Catches {@link BusinessException} thrown when a business rule is violated.
     * Returns an HTTP 400 Bad Request response with the exception message.
     * </p>
     *
     * @param ex the business exception containing the error message
     * @return a {@link ResponseEntity} with HTTP status 400 and an error response body
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Handles authentication failures.
     * <p>
     * Catches {@link BadCredentialsException} thrown when a user provides
     * invalid credentials during login. Returns an HTTP 401 Unauthorized
     * response with a generic error message.
     * </p>
     *
     * @param ex the bad credentials exception
     * @return a {@link ResponseEntity} with HTTP status 401 and an error response body
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid username or password"));
    }

    /**
     * Handles access denial exceptions.
     * <p>
     * Catches {@link AccessDeniedException} thrown when a user attempts to
     * access a resource they do not have permission for. Returns an HTTP 403
     * Forbidden response.
     * </p>
     *
     * @param ex the access denied exception
     * @return a {@link ResponseEntity} with HTTP status 403 and an error response body
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Access denied"));
    }

    /**
     * Handles validation failures for request bodies.
     * <p>
     * Catches {@link MethodArgumentNotValidException} thrown when validation
     * annotations (e.g., {@code @NotBlank}, {@code @Size}) are violated.
     * Returns an HTTP 400 Bad Request response with a concatenated list of
     * all validation error messages.
     * </p>
     *
     * @param ex the validation exception containing field errors
     * @return a {@link ResponseEntity} with HTTP status 400 and an error response body
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }

    /**
     * Handles any uncaught exceptions as a fallback.
     * <p>
     * Catches all other exceptions that are not explicitly handled by other
     * methods. Returns an HTTP 500 Internal Server Error response with a
     * generic error message. This prevents internal exception details from
     * being exposed to the client.
     * </p>
     *
     * @param ex the uncaught exception
     * @return a {@link ResponseEntity} with HTTP status 500 and an error response body
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Unexpected error: " + ex.getMessage()));
    }
}