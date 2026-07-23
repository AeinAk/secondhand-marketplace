package com.marketplace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Security configuration for the marketplace application.
 * <p>
 * This class configures Spring Security with JWT-based authentication,
 * stateless session management, and role-based authorization. It defines:
 * <ul>
 *   <li>Public endpoints (permitAll) for health checks, authentication,
 *       listing browsing, categories, cities, uploads, and seller ratings</li>
 *   <li>Protected admin endpoints requiring the {@code ADMIN} role</li>
 *   <li>A JWT authentication filter that intercepts requests to validate tokens</li>
 *   <li>CORS configuration to allow cross-origin requests from frontend clients</li>
 *   <li>BCrypt password encoding for secure password storage</li>
 * </ul>
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Constructs a SecurityConfig with the required JWT authentication filter.
     *
     * @param jwtAuthFilter the JWT filter for token validation
     */
    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Configures the security filter chain.
     * <p>
     * Sets up the following security policies:
     * <ul>
     *   <li>Disables CSRF protection (stateless API)</li>
     *   <li>Enables CORS with configured source</li>
     *   <li>Sets session management to STATELESS (no server-side sessions)</li>
     *   <li>Defines public and protected endpoints with appropriate access rules</li>
     *   <li>Adds the JWT authentication filter before the default UsernamePasswordAuthenticationFilter</li>
     * </ul>
     * </p>
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/health", "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/listings/active", "/api/listings/search",
                                "/api/categories/**", "/api/cities/**", "/api/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/listings/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/ratings/seller/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Provides a BCrypt password encoder for hashing user passwords.
     * <p>
     * BCrypt is a strong, adaptive hashing function that includes a salt
     * and is resistant to brute-force attacks.
     * </p>
     *
     * @return a {@link PasswordEncoder} instance using BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the {@link AuthenticationManager} bean for use in custom authentication logic.
     *
     * @param configuration the {@link AuthenticationConfiguration} to obtain the manager from
     * @return the {@link AuthenticationManager} instance
     * @throws Exception if the manager cannot be retrieved
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings.
     * <p>
     * Allows requests from any origin (for development purposes) and permits
     * common HTTP methods (GET, POST, PUT, DELETE, OPTIONS). Credentials are
     * allowed so that the frontend can send the JWT token in the Authorization header.
     * </p>
     *
     * @return a {@link CorsConfigurationSource} with permissive CORS settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}