package com.marketplace.backend.service;

import com.marketplace.backend.dto.UserDto;
import com.marketplace.backend.entity.User;
import com.marketplace.backend.entity.UserRole;
import com.marketplace.backend.exception.BusinessException;
import com.marketplace.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for user management operations.
 * <p>
 * Provides business logic for retrieving user information, managing user accounts,
 * and blocking/unblocking users. This service interacts with the Spring Security
 * context to access the currently authenticated user and includes administrative
 * functions for managing all users.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    /**
     * Constructs a UserService with the required repository.
     *
     * @param userRepository the repository for user data access
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves the currently authenticated user from the Spring Security context.
     * <p>
     * This method extracts the username from the {@link Authentication} object
     * in the {@link SecurityContextHolder} and fetches the corresponding user
     * from the database. If the user is not authenticated or the user does not
     * exist, a {@link BusinessException} is thrown.
     * </p>
     *
     * @return the {@link User} entity of the currently authenticated user
     * @throws BusinessException if the user is not authenticated or the user is not found
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("Not authenticated");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    /**
     * Retrieves all registered users.
     * <p>
     * Returns a list of all users in the system, mapped to DTO objects.
     * This method is typically used by administrators to view and manage
     * all user accounts.
     * </p>
     *
     * @return a list of {@link UserDto} objects representing all users
     */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    /**
     * Blocks or unblocks a user account.
     * <p>
     * Admins can block or unblock users to manage access to the system.
     * Blocked users cannot log in or perform any operations. Admins cannot
     * block other admin users. If the user does not exist, a
     * {@link BusinessException} is thrown.
     * </p>
     *
     * @param userId  the unique identifier of the user to block or unblock
     * @param blocked {@code true} to block the user, {@code false} to unblock
     * @return the updated {@link UserDto} with the new block status
     * @throws BusinessException if the user does not exist or the user is an admin
     */
    public UserDto blockUser(Long userId, boolean blocked) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));
        if (user.getRole() == UserRole.ADMIN) {
            throw new BusinessException("Cannot block admin users");
        }
        user.setBlocked(blocked);
        return toDto(userRepository.save(user));
    }

    /**
     * Converts a User entity to a UserDto.
     * <p>
     * Maps all user fields to a DTO for safe data transfer to the frontend,
     * excluding sensitive information like the password hash. The blocked
     * status is included for account management purposes.
     * </p>
     *
     * @param user the User entity to convert
     * @return the corresponding {@link UserDto}
     */
    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setBlocked(user.isBlocked());
        return dto;
    }
}