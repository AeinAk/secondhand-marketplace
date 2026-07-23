package com.marketplace.backend.controller;

import com.marketplace.backend.dto.AuthResponse;
import com.marketplace.backend.dto.LoginRequest;
import com.marketplace.backend.dto.RegisterRequest;
import com.marketplace.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication operations.
 * <p>
 * Provides endpoints for user registration and login. All endpoints in this
 * controller are publicly accessible and do not require prior authentication.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructs an {@code AuthController} with the required authentication service.
     *
     * @param authService the service handling registration and login logic
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user account.
     * <p>
     * Validates the provided registration data (username, email, password, etc.)
     * and creates a new user in the system. On success, a JWT token is returned
     * for immediate authentication.
     * </p>
     *
     * @param request the registration request containing user details
     * @return an {@link AuthResponse} containing the JWT token and user information
     * @throws com.marketplace.backend.exception.BusinessException if the username or email is already taken
     */
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * Authenticates a user and issues a JWT token.
     * <p>
     * Verifies the provided username and password against the stored credentials.
     * If successful, returns a JWT token that must be included in subsequent
     * authenticated requests.
     * </p>
     *
     * @param request the login request containing username and password
     * @return an {@link AuthResponse} containing the JWT token and user information
     * @throws com.marketplace.backend.exception.BusinessException if the credentials are invalid or the account is blocked
     */
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}