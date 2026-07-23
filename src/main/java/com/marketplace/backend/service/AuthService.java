package com.marketplace.backend.service;

import com.marketplace.backend.dto.AuthResponse;
import com.marketplace.backend.dto.LoginRequest;
import com.marketplace.backend.dto.RegisterRequest;
import com.marketplace.backend.entity.User;
import com.marketplace.backend.entity.UserRole;
import com.marketplace.backend.exception.BusinessException;
import com.marketplace.backend.repository.UserRepository;
import com.marketplace.config.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for authentication operations.
 * <p>
 * Provides business logic for user registration and login. This service handles
 * user creation, password encoding, JWT token generation, and authentication
 * validation. It ensures that usernames and emails are unique before registration,
 * and validates credentials during login.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Constructs an AuthService with the required dependencies.
     *
     * @param userRepository   the repository for user data access
     * @param passwordEncoder  the encoder for hashing passwords
     * @param jwtUtil          the utility for JWT token generation and validation
     */
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user account.
     * <p>
     * Validates that the username and email are not already in use, creates a new
     * user with the provided information, encodes the password using BCrypt,
     * assigns the USER role, and generates a JWT token for immediate authentication.
     * </p>
     *
     * @param request the registration request containing user details
     * @return an {@link AuthResponse} containing the JWT token and user information
     * @throws BusinessException if the username or email is already taken
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.USER);
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole(), "Registration successful");
    }

    /**
     * Authenticates a user and issues a JWT token.
     * <p>
     * Validates the provided credentials by checking if the user exists, the account
     * is not blocked, and the password matches. On successful authentication, a
     * JWT token is generated for subsequent authenticated requests.
     * </p>
     *
     * @param request the login request containing username and password
     * @return an {@link AuthResponse} containing the JWT token and user information
     * @throws BusinessException if the credentials are invalid or the account is blocked
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Invalid username or password"));

        if (user.isBlocked()) {
            throw new BusinessException("Your account has been blocked");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole(), "Login successful");
    }
}