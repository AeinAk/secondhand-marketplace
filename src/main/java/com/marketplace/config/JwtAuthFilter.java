package com.marketplace.config;

import com.marketplace.backend.entity.User;
import com.marketplace.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT authentication filter that intercepts HTTP requests and validates JWT tokens.
 * <p>
 * This filter is invoked once per request (as it extends {@link OncePerRequestFilter})
 * and attempts to extract a JWT token from the {@code Authorization} header.
 * If a valid token is found and no authentication exists in the security context,
 * it loads the user from the database, checks if the user is blocked, and sets
 * the authentication in the {@link SecurityContextHolder} with the user's roles.
 * </p>
 * <p>
 * The filter is a critical component of the security architecture, ensuring that
 * all protected endpoints are only accessible by authenticated users with valid tokens.
 * Blocked users are automatically prevented from authenticating, even if their token is valid.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    /**
     * Constructs a JwtAuthFilter with the required dependencies.
     *
     * @param jwtUtil        the utility for JWT token validation and extraction
     * @param userRepository the repository for loading user details
     */
    public JwtAuthFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    /**
     * Filters incoming requests to authenticate users using JWT tokens.
     * <p>
     * This method is called for every HTTP request. It checks for a Bearer token
     * in the {@code Authorization} header. If a token is present and valid, it
     * extracts the username, loads the user from the database, and sets the
     * authentication in the security context (only if the user is not blocked).
     * </p>
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain to continue with the request
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.isTokenValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                String username = jwtUtil.extractUsername(token);
                userRepository.findByUsername(username).ifPresent(user -> authenticateUser(user, request));
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Authenticates a user in the security context if the user is not blocked.
     * <p>
     * Creates an {@link UsernamePasswordAuthenticationToken} with the user's
     * username and authorities (role prefixed with "ROLE_"), and sets it in
     * the {@link SecurityContextHolder}. If the user is blocked, no authentication
     * is performed, effectively denying access.
     * </p>
     *
     * @param user    the user to authenticate
     * @param request the HTTP request used to build authentication details
     */
    private void authenticateUser(User user, HttpServletRequest request) {
        if (user.isBlocked()) {
            return;
        }
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        var authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}