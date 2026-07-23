package com.marketplace.backend.service;

import com.marketplace.backend.dto.ListingDto;
import com.marketplace.backend.entity.Favorite;
import com.marketplace.backend.entity.Listing;
import com.marketplace.backend.entity.User;
import com.marketplace.backend.exception.BusinessException;
import com.marketplace.backend.repository.FavoriteRepository;
import com.marketplace.backend.repository.ListingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing user favorites.
 * <p>
 * Provides business logic for adding, removing, and retrieving favorite listings
 * for the currently authenticated user. Users can mark listings as favorites
 * to quickly access them later. Each user-listing pair is unique, preventing
 * duplicate favorites.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ListingRepository listingRepository;
    private final ListingService listingService;
    private final UserService userService;

    /**
     * Constructs a FavoriteService with the required dependencies.
     *
     * @param favoriteRepository the repository for favorite data access
     * @param listingRepository  the repository for listing data access
     * @param listingService     the service for listing-related operations
     * @param userService        the service for user-related operations
     */
    public FavoriteService(FavoriteRepository favoriteRepository,
                           ListingRepository listingRepository,
                           ListingService listingService,
                           UserService userService) {
        this.favoriteRepository = favoriteRepository;
        this.listingRepository = listingRepository;
        this.listingService = listingService;
        this.userService = userService;
    }

    /**
     * Retrieves all favorite listings for the currently authenticated user.
     * <p>
     * Returns a list of full listing details for all ads that the user has marked
     * as favorites. The listings are ordered by the date they were added.
     * </p>
     *
     * @return a list of {@link ListingDto} objects representing the user's favorite listings
     * @throws BusinessException if the user is not authenticated
     */
    public List<ListingDto> getFavorites() {
        User user = userService.getCurrentUser();
        return favoriteRepository.findByUserId(user.getId()).stream()
                .map(Favorite::getListing)
                .map(listing -> listingService.toDto(listing, user.getId()))
                .toList();
    }

    /**
     * Adds a listing to the user's favorites.
     * <p>
     * Validates that the listing exists and is not already in the user's favorites.
     * If the listing is already favorited, a {@link BusinessException} is thrown.
     * The operation is transactional to ensure data consistency.
     * </p>
     *
     * @param listingId the unique identifier of the listing to add to favorites
     * @throws BusinessException if the listing does not exist,
     *         if the user is not authenticated, or if the listing is already favorited
     */
    @Transactional
    public void addFavorite(Long listingId) {
        User user = userService.getCurrentUser();
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new BusinessException("Listing not found"));
        if (favoriteRepository.existsByUserIdAndListingId(user.getId(), listingId)) {
            throw new BusinessException("Listing already in favorites");
        }
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setListing(listing);
        favoriteRepository.save(favorite);
    }

    /**
     * Removes a listing from the user's favorites.
     * <p>
     * Validates that the listing exists and is actually in the user's favorites.
     * If the listing is not favorited, a {@link BusinessException} is thrown.
     * The operation is transactional to ensure data consistency.
     * </p>
     *
     * @param listingId the unique identifier of the listing to remove from favorites
     * @throws BusinessException if the listing does not exist,
     *         if the user is not authenticated, or if the listing is not in the user's favorites
     */
    @Transactional
    public void removeFavorite(Long listingId) {
        User user = userService.getCurrentUser();
        Favorite favorite = favoriteRepository.findByUserAndListing(user,
                        listingRepository.findById(listingId)
                                .orElseThrow(() -> new BusinessException("Listing not found")))
                .orElseThrow(() -> new BusinessException("Favorite not found"));
        favoriteRepository.delete(favorite);
    }
}