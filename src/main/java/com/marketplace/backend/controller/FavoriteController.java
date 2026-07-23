package com.marketplace.backend.controller;

import com.marketplace.backend.dto.ListingDto;
import com.marketplace.backend.service.FavoriteService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing user favorites.
 * <p>
 * Provides endpoints for retrieving, adding, and removing favorite listings for the
 * currently authenticated user. All endpoints require user authentication.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * Constructs a {@code FavoriteController} with the required favorite service.
     *
     * @param favoriteService the service handling favorite management logic
     */
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * Retrieves all favorite listings for the currently authenticated user.
     * <p>
     * Returns a list of full listing details for all ads that the user has marked
     * as favorites, ordered by the date they were added (most recent first).
     * </p>
     *
     * @return a list of {@link ListingDto} objects representing the user's favorite listings
     */
    @GetMapping
    public List<ListingDto> getFavorites() {
        return favoriteService.getFavorites();
    }

    /**
     * Adds a listing to the user's favorites.
     * <p>
     * The user must be authenticated. If the listing is already in the user's favorites,
     * a {@code BusinessException} is thrown. The operation is idempotent in the sense that
     * adding the same listing again is prevented.
     * </p>
     *
     * @param listingId the unique identifier of the listing to add to favorites
     * @throws com.marketplace.backend.exception.BusinessException if the listing does not exist,
     *         if the user is not authenticated, or if the listing is already favorited
     */
    @PostMapping("/{listingId}")
    public void addFavorite(@PathVariable Long listingId) {
        favoriteService.addFavorite(listingId);
    }

    /**
     * Removes a listing from the user's favorites.
     * <p>
     * The user must be authenticated. If the listing is not in the user's favorites,
     * a {@code BusinessException} is thrown. This operation is permanent and cannot be undone.
     * </p>
     *
     * @param listingId the unique identifier of the listing to remove from favorites
     * @throws com.marketplace.backend.exception.BusinessException if the listing does not exist,
     *         if the user is not authenticated, or if the listing is not in the user's favorites
     */
    @DeleteMapping("/{listingId}")
    public void removeFavorite(@PathVariable Long listingId) {
        favoriteService.removeFavorite(listingId);
    }
}