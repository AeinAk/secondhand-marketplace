package com.marketplace.backend.controller;

import com.marketplace.backend.dto.AdminReviewRequest;
import com.marketplace.backend.dto.ListingDto;
import com.marketplace.backend.dto.UserDto;
import com.marketplace.backend.service.ListingService;
import com.marketplace.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for administrative operations.
 * <p>
 * All endpoints in this controller are restricted to users with the {@code ADMIN} role.
 * Provides functionality for managing listings, user accounts, and content moderation.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ListingService listingService;
    private final UserService userService;

    /**
     * Constructs an {@code AdminController} with required services.
     *
     * @param listingService the service for listing management operations
     * @param userService    the service for user management operations
     */
    public AdminController(ListingService listingService, UserService userService) {
        this.listingService = listingService;
        this.userService = userService;
    }

    /**
     * Retrieves all listings that are pending administrative review.
     * <p>
     * Only listings with status {@code PENDING} are returned.
     * </p>
     *
     * @return a list of {@link ListingDto} objects representing pending listings
     */
    @GetMapping("/listings/pending")
    public List<ListingDto> pendingListings() {
        return listingService.getPendingListings();
    }

    /**
     * Reviews a listing by approving or rejecting it.
     * <p>
     * The admin provides a decision (APPROVED or REJECTED) and an optional comment.
     * The listing's status is updated accordingly.
     * </p>
     *
     * @param id      the unique identifier of the listing to review
     * @param request the review request containing the decision and optional comment
     * @return the updated {@link ListingDto} reflecting the new status
     */
    @PostMapping("/listings/{id}/review")
    public ListingDto reviewListing(@PathVariable Long id, @Valid @RequestBody AdminReviewRequest request) {
        return listingService.reviewListing(id, request);
    }

    /**
     * Deletes a listing from the system.
     * <p>
     * This operation is allowed for administrators to remove inappropriate content.
     * </p>
     *
     * @param id the unique identifier of the listing to delete
     */
    @DeleteMapping("/listings/{id}")
    public void deleteListing(@PathVariable Long id) {
        listingService.delete(id);
    }

    /**
     * Retrieves a list of all registered users.
     * <p>
     * This endpoint provides an overview of all user accounts, including their roles,
     * contact information, and block status.
     * </p>
     *
     * @return a list of {@link UserDto} objects representing all users
     */
    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    /**
     * Blocks or unblocks a user account.
     * <p>
     * When a user is blocked, they are unable to log in or perform any operations
     * within the system. The block status is toggled based on the provided parameter.
     * </p>
     *
     * @param id      the unique identifier of the user to block or unblock
     * @param blocked {@code true} to block the user, {@code false} to unblock them
     * @return the updated {@link UserDto} with the new block status
     */
    @PutMapping("/users/{id}/block")
    public UserDto blockUser(@PathVariable Long id, @RequestParam boolean blocked) {
        return userService.blockUser(id, blocked);
    }
}