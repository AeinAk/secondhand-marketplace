package com.marketplace.backend.dto;

/**
 * Standard response wrapper for API operations.
 * <p>
 * Provides a consistent structure for all API responses, indicating whether
 * an operation succeeded and containing an appropriate message. This class
 * is used for non-payload responses (e.g., success confirmations, error messages)
 * across the entire API layer.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class ApiResponse {

    /**
     * Indicates whether the operation was successful.
     */
    private boolean success;

    /**
     * A human-readable message describing the result of the operation.
     */
    private String message;

    /**
     * Default constructor required for deserialization.
     */
    public ApiResponse() {
    }

    /**
     * Constructs an ApiResponse with the specified success status and message.
     *
     * @param success {@code true} if the operation was successful, {@code false} otherwise
     * @param message a descriptive message about the operation result
     */
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Creates a success response with the given message.
     * <p>
     * Convenience factory method for constructing a positive response.
     * </p>
     *
     * @param message the success message
     * @return an {@link ApiResponse} with {@code success = true}
     */
    public static ApiResponse ok(String message) {
        return new ApiResponse(true, message);
    }

    /**
     * Creates an error response with the given message.
     * <p>
     * Convenience factory method for constructing a negative response.
     * </p>
     *
     * @param message the error message
     * @return an {@link ApiResponse} with {@code success = false}
     */
    public static ApiResponse error(String message) {
        return new ApiResponse(false, message);
    }

    /**
     * Returns whether the operation was successful.
     *
     * @return {@code true} if the operation succeeded, {@code false} otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success status of the response.
     *
     * @param success {@code true} for a successful operation, {@code false} otherwise
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Returns the response message.
     *
     * @return the message describing the operation result
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the response message.
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}