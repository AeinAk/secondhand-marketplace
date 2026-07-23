package com.marketplace.backend.controller;

import com.marketplace.backend.dto.AdminReviewRequest;
import com.marketplace.backend.dto.ListingDto;
import com.marketplace.backend.dto.ListingSearchRequest;
import com.marketplace.backend.service.ListingService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

/**
 * REST controller for managing listings (advertisements).
 * <p>
 * Provides comprehensive endpoints for listing operations including creation,
 * retrieval, update, deletion, search, and status management. Most endpoints
 * require user authentication, while public endpoints are available for
 * browsing active listings and searching.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;

    /**
     * Constructs a {@code ListingController} with the required listing service.
     *
     * @param listingService the service handling listing business logic
     */
    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    /**
     * Retrieves all active listings.
     * <p>
     * Returns all listings with status {@code ACTIVE}. This endpoint is publicly
     * accessible and does not require authentication.
     * </p>
     *
     * @return a list of {@link ListingDto} objects representing all active listings,
     *         ordered by creation date (newest first)
     */
    @GetMapping("/active")
    public List<ListingDto> getActiveListings() {
        return listingService.getActiveListings(new ListingSearchRequest());
    }

    /**
     * Performs an advanced search on listings.
     * <p>
     * Accepts a search request with various optional filters including keyword,
     * category, city, price range, and specifications. Only returns listings
     * with status {@code ACTIVE}. This endpoint is publicly accessible.
     * </p>
     *
     * @param request the search criteria containing optional filters
     * @return a list of {@link ListingDto} objects matching the search criteria
     */
    @PostMapping("/search")
    public List<ListingDto> search(@RequestBody ListingSearchRequest request) {
        return listingService.getActiveListings(request);
    }

    /**
     * Retrieves all listings belonging to the currently authenticated user.
     * <p>
     * Returns all listings where the current user is the seller, regardless of
     * their status (PENDING, ACTIVE, REJECTED, SOLD). Requires authentication.
     * </p>
     *
     * @return a list of {@link ListingDto} objects representing the user's own listings
     */
    @GetMapping("/mine")
    public List<ListingDto> getMyListings() {
        return listingService.getMyListings();
    }

    /**
     * Retrieves a specific listing by its unique identifier.
     * <p>
     * Returns full listing details including images and seller information.
     * Access restrictions apply: only ACTIVE listings are visible to the public,
     * while owners and admins can view listings in any status.
     * </p>
     *
     * @param id the unique identifier of the listing
     * @return a {@link ListingDto} containing the full listing details
     * @throws com.marketplace.backend.exception.BusinessException if the listing does not exist
     *         or if access is denied based on status and role
     */
    @GetMapping("/{id}")
    public ListingDto getById(@PathVariable Long id) {
        return listingService.getById(id);
    }

    /**
     * Creates a new listing.
     * <p>
     * Accepts multipart form data containing listing details and optional images.
     * The listing is initially saved with status {@code PENDING} and must be
     * reviewed by an admin before becoming active. Requires authentication.
     * </p>
     *
     * @param listing the listing data (title, description, price, category, city, etc.)
     * @param images  optional list of image files to upload with the listing
     * @return the created {@link ListingDto} with generated ID and status {@code PENDING}
     * @throws com.marketplace.backend.exception.BusinessException if validation fails,
     *         if the category or city does not exist, or if the user is not authenticated
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ListingDto create(@RequestPart("listing") @Valid ListingDto listing,
                             @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return listingService.create(listing, images == null ? Collections.emptyList() : images);
    }

    /**
     * Updates an existing listing.
     * <p>
     * Accepts multipart form data with updated listing details and optional new images.
     * Only the seller can edit their own listings. If the listing was previously
     * rejected, its status is reset to {@code PENDING} for re-review.
     * SOLD listings cannot be edited. Requires authentication.
     * </p>
     *
     * @param id      the unique identifier of the listing to update
     * @param listing the updated listing data
     * @param images  optional list of new image files to add to the listing
     * @return the updated {@link ListingDto}
     * @throws com.marketplace.backend.exception.BusinessException if the listing does not exist,
     *         if the user is not the seller, if the listing is SOLD, or if validation fails
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ListingDto update(@PathVariable Long id,
                             @RequestPart("listing") @Valid ListingDto listing,
                             @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return listingService.update(id, listing, images);
    }

    /**
     * Deletes a listing.
     * <p>
     * Only the seller or an admin can delete a listing. This is a hard delete
     * and permanently removes the listing from the system. Requires authentication.
     * </p>
     *
     * @param id the unique identifier of the listing to delete
     * @throws com.marketplace.backend.exception.BusinessException if the listing does not exist,
     *         if the user is not authorized to delete it
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        listingService.delete(id);
    }

    /**
     * Marks a listing as sold.
     * <p>
     * Only the seller can mark their own listing as sold. The status changes
     * to {@code SOLD}, which prevents further edits and hides the listing from
     * public search results. Requires authentication.
     * </p>
     *
     * @param id the unique identifier of the listing to mark as sold
     * @return the updated {@link ListingDto} with status {@code SOLD}
     * @throws com.marketplace.backend.exception.BusinessException if the listing does not exist,
     *         if the user is not the seller, or if the listing is not in a valid state
     */
    @PutMapping("/{id}/sold")
    public ListingDto markSold(@PathVariable Long id) {
        return listingService.markSold(id);
    }
}