package com.marketplace.backend.controller;

import com.marketplace.backend.dto.RatingRequest;
import com.marketplace.backend.dto.SellerRatingDto;
import com.marketplace.backend.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing seller ratings.
 * <p>
 * Provides endpoints for submitting ratings and retrieving rating history
 * for sellers. Ratings allow buyers to provide feedback on their transactions
 * and help build seller reputation within the marketplace.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    /**
     * Constructs a {@code RatingController} with the required rating service.
     *
     * @param ratingService the service handling rating business logic
     */
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Submits a rating for a seller based on a specific listing.
     * <p>
     * The rating request must include the listing ID, a rating score (1-5),
     * and an optional review text. The authenticated user becomes the reviewer.
     * A user cannot rate themselves or submit multiple ratings for the same
     * listing. Requires user authentication.
     * </p>
     *
     * @param request the rating request containing the listing ID, score, and optional review text
     * @return a {@link SellerRatingDto} containing the saved rating details
     * @throws com.marketplace.backend.exception.BusinessException if the listing does not exist,
     *         if the user is the seller, if a rating already exists for this listing by this user,
     *         or if the rating score is outside the valid range (1-5)
     */
    @PostMapping
    public SellerRatingDto rateSeller(@Valid @RequestBody RatingRequest request) {
        return ratingService.rateSeller(request);
    }

    /**
     * Retrieves all ratings submitted for a specific seller.
     * <p>
     * Returns a list of all ratings that the seller has received, including
     * reviewer information, listing details, rating scores, and review texts.
     * This endpoint is publicly accessible and does not require authentication.
     * </p>
     *
     * @param sellerId the unique identifier of the seller whose ratings are to be retrieved
     * @return a list of {@link SellerRatingDto} objects representing all ratings for the seller,
     *         ordered by creation date (most recent first)
     * @throws com.marketplace.backend.exception.BusinessException if the seller does not exist
     */
    @GetMapping("/seller/{sellerId}")
    public List<SellerRatingDto> getSellerRatings(@PathVariable Long sellerId) {
        return ratingService.getSellerRatings(sellerId);
    }
}