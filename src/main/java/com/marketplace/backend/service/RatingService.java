package com.marketplace.backend.service;

import com.marketplace.backend.dto.RatingRequest;
import com.marketplace.backend.dto.SellerRatingDto;
import com.marketplace.backend.dto.SellerRatingSummaryDto;
import com.marketplace.backend.entity.Listing;
import com.marketplace.backend.entity.SellerRating;
import com.marketplace.backend.entity.User;
import com.marketplace.backend.exception.BusinessException;
import com.marketplace.backend.repository.ListingRepository;
import com.marketplace.backend.repository.SellerRatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing seller ratings.
 * <p>
 * Provides business logic for submitting ratings, retrieving rating history for sellers,
 * and calculating summary statistics (average and count) for seller reputation.
 * This service ensures that users can only rate a seller once per listing,
 * cannot rate themselves, and that all ratings are stored with a timestamp.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Service
public class RatingService {

    private final SellerRatingRepository sellerRatingRepository;
    private final ListingRepository listingRepository;
    private final UserService userService;

    /**
     * Constructs a RatingService with the required dependencies.
     *
     * @param sellerRatingRepository the repository for seller rating data access
     * @param listingRepository      the repository for listing data access
     * @param userService            the service for user-related operations
     */
    public RatingService(SellerRatingRepository sellerRatingRepository,
                         ListingRepository listingRepository,
                         UserService userService) {
        this.sellerRatingRepository = sellerRatingRepository;
        this.listingRepository = listingRepository;
        this.userService = userService;
    }

    /**
     * Submits a rating for a seller based on a specific listing.
     * <p>
     * The authenticated user becomes the reviewer. The rating is validated to ensure:
     * <ul>
     *   <li>The listing exists.</li>
     *   <li>The reviewer is not the seller (self-rating is prohibited).</li>
     *   <li>The reviewer has not already rated this seller for the same listing.</li>
     * </ul>
     * The rating score must be between 1 and 5. The review text is optional.
     * </p>
     *
     * @param request the rating request containing listing ID, score, and optional review text
     * @return a {@link SellerRatingDto} containing the saved rating details
     * @throws BusinessException if the listing does not exist,
     *         if the user is the seller, if a rating already exists for this listing by this user,
     *         or if the rating score is outside the valid range (1-5)
     */
    @Transactional
    public SellerRatingDto rateSeller(RatingRequest request) {
        User reviewer = userService.getCurrentUser();
        Listing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new BusinessException("Listing not found"));
        if (listing.getSeller().getId().equals(reviewer.getId())) {
            throw new BusinessException("You cannot rate yourself");
        }
        if (sellerRatingRepository.findByReviewerIdAndListingId(reviewer.getId(), listing.getId()).isPresent()) {
            throw new BusinessException("You already rated this seller for this listing");
        }

        SellerRating rating = new SellerRating();
        rating.setSeller(listing.getSeller());
        rating.setReviewer(reviewer);
        rating.setListing(listing);
        rating.setRating(request.getRating());
        rating.setReviewText(request.getReviewText());
        sellerRatingRepository.save(rating);
        return toDto(rating);
    }

    /**
     * Retrieves all ratings submitted for a specific seller.
     * <p>
     * Returns a list of all ratings that the seller has received, including
     * reviewer information, listing details, rating scores, and review texts.
     * This method is publicly accessible and does not require authentication.
     * </p>
     *
     * @param sellerId the unique identifier of the seller whose ratings are to be retrieved
     * @return a list of {@link SellerRatingDto} objects representing all ratings for the seller,
     *         ordered by creation date (most recent first)
     * @throws BusinessException if the seller does not exist (handled by repository)
     */
    public List<SellerRatingDto> getSellerRatings(Long sellerId) {
        return sellerRatingRepository.findBySellerId(sellerId).stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Converts a SellerRating entity to a SellerRatingDto.
     * <p>
     * This method maps all entity fields to a DTO for safe data transfer
     * to the frontend, excluding internal JPA-related fields.
     * </p>
     *
     * @param rating the SellerRating entity to convert
     * @return the corresponding {@link SellerRatingDto}
     */
    private SellerRatingDto toDto(SellerRating rating) {
        SellerRatingDto dto = new SellerRatingDto();
        dto.setId(rating.getId());
        dto.setSellerId(rating.getSeller().getId());
        dto.setSellerUsername(rating.getSeller().getUsername());
        dto.setReviewerId(rating.getReviewer().getId());
        dto.setReviewerUsername(rating.getReviewer().getUsername());
        dto.setListingId(rating.getListing().getId());
        dto.setListingTitle(rating.getListing().getTitle());
        dto.setRating(rating.getRating());
        dto.setReviewText(rating.getReviewText());
        dto.setCreatedAt(rating.getCreatedAt());
        return dto;
    }

    /**
     * Calculates the average rating and total count for a seller.
     * <p>
     * Returns a summary DTO containing the average score (0.0 if no ratings yet)
     * and the total number of ratings received. This method is used by the
     * {@link ListingService} to populate seller rating information in listing DTOs.
     * </p>
     *
     * @param sellerId the unique identifier of the seller
     * @return a {@link SellerRatingSummaryDto} with average and count
     */
    public SellerRatingSummaryDto getRatingSummary(Long sellerId) {
        Double avg = sellerRatingRepository.findAverageRatingBySellerId(sellerId);
        Long count = sellerRatingRepository.countRatingsBySellerId(sellerId);
        return new SellerRatingSummaryDto(
                avg != null ? avg : 0.0,
                count != null ? count : 0L
        );
    }
}