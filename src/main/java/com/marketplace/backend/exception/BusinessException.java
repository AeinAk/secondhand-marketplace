package com.marketplace.backend.exception;

/**
 * Exception thrown for business logic violations in the marketplace application.
 * <p>
 * This runtime exception is used to indicate that a business rule has been
 * violated, such as duplicate username, invalid operation state, or permission
 * issues. It is intended to be caught and handled by the global exception handler
 * {@link com.marketplace.backend.exception.GlobalExceptionHandler}, which returns
 * appropriate HTTP responses to the client.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class BusinessException extends RuntimeException {

    /**
     * Constructs a new BusinessException with the specified detail message.
     *
     * @param message the detail message explaining the business rule violation
     */
    public BusinessException(String message) {
        super(message);
    }
}