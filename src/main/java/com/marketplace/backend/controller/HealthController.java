package com.marketplace.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for service health checks.
 * <p>
 * Provides a simple endpoint to verify that the backend application is running
 * and responsive. This endpoint can be used by monitoring tools, load balancers,
 * or frontend applications to check the service status before making API calls.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * Performs a health check on the backend service.
     * <p>
     * Returns a simple success message indicating that the backend is operational.
     * No authentication is required for this endpoint, making it suitable for
     * external monitoring and quick validation of service availability.
     * </p>
     *
     * @return a {@code String} message "Backend is running" confirming the service is active
     */
    @GetMapping("/health")
    public String health() {
        return "Backend is running";
    }
}