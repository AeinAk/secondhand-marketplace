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

@Service
public class RatingService {

    private final SellerRatingRepository sellerRatingRepository;
    private final ListingRepository listingRepository;
    private final UserService userService;

    public RatingService(SellerRatingRepository sellerRatingRepository,
                         ListingRepository listingRepository,
                         UserService userService) {
        this.sellerRatingRepository = sellerRatingRepository;
        this.listingRepository = listingRepository;
        this.userService = userService;
    }

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

    public List<SellerRatingDto> getSellerRatings(Long sellerId) {
        return sellerRatingRepository.findBySellerId(sellerId).stream()
                .map(this::toDto)
                .toList();
    }

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

    public SellerRatingSummaryDto getRatingSummary(Long sellerId) {
        Double avg = sellerRatingRepository.findAverageRatingBySellerId(sellerId);
        Long count = sellerRatingRepository.countRatingsBySellerId(sellerId);
        return new SellerRatingSummaryDto(
                avg != null ? avg : 0.0,
                count != null ? count : 0L
        );
    }
}
