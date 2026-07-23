package com.marketplace.backend.service;

import com.marketplace.backend.dto.AdminReviewRequest;
import com.marketplace.backend.dto.ListingDto;
import com.marketplace.backend.dto.ListingSearchRequest;
import com.marketplace.backend.dto.SellerRatingSummaryDto;
import com.marketplace.backend.entity.AdminReview;
import com.marketplace.backend.entity.Category;
import com.marketplace.backend.entity.City;
import com.marketplace.backend.entity.Listing;
import com.marketplace.backend.entity.ListingImage;
import com.marketplace.backend.entity.ListingStatus;
import com.marketplace.backend.entity.ReviewDecision;
import com.marketplace.backend.entity.User;
import com.marketplace.backend.exception.BusinessException;
import com.marketplace.backend.repository.AdminReviewRepository;
import com.marketplace.backend.repository.FavoriteRepository;
import com.marketplace.backend.repository.ListingImageRepository;
import com.marketplace.backend.repository.ListingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing listings (advertisements).
 * <p>
 * This service provides comprehensive business logic for listing operations
 * including creation, retrieval, update, deletion, search, and status management.
 * It handles image uploads, seller rating aggregation, and administrative reviews.
 * Access control is enforced based on user roles and ownership.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final ListingImageRepository listingImageRepository;
    private final CategoryService categoryService;
    private final CityService cityService;
    private final UserService userService;
    private final FavoriteRepository favoriteRepository;
    private final AdminReviewRepository adminReviewRepository;
    private final FileStorageService fileStorageService;
    private final RatingService ratingService;

    /**
     * Constructs a ListingService with all required dependencies.
     *
     * @param listingRepository       the repository for listing data access
     * @param listingImageRepository  the repository for listing image data access
     * @param categoryService         the service for category operations
     * @param cityService             the service for city operations
     * @param userService             the service for user operations
     * @param favoriteRepository      the repository for favorite data access
     * @param adminReviewRepository   the repository for admin review data access
     * @param fileStorageService      the service for file storage operations
     * @param ratingService           the service for rating operations
     */
    public ListingService(ListingRepository listingRepository,
                          ListingImageRepository listingImageRepository,
                          CategoryService categoryService,
                          CityService cityService,
                          UserService userService,
                          FavoriteRepository favoriteRepository,
                          AdminReviewRepository adminReviewRepository,
                          FileStorageService fileStorageService,
                          RatingService ratingService) {
        this.listingRepository = listingRepository;
        this.listingImageRepository = listingImageRepository;
        this.categoryService = categoryService;
        this.cityService = cityService;
        this.userService = userService;
        this.favoriteRepository = favoriteRepository;
        this.adminReviewRepository = adminReviewRepository;
        this.fileStorageService = fileStorageService;
        this.ratingService = ratingService;
    }

    /**
     * Retrieves active listings based on search criteria.
     * <p>
     * Returns all active listings that match the provided search filters.
     * If no search criteria are provided, all active listings are returned.
     * The search uses JPA Specifications for dynamic query building.
     * </p>
     *
     * @param search the search criteria containing optional filters
     * @return a list of {@link ListingDto} objects representing matching active listings
     */
    public List<ListingDto> getActiveListings(ListingSearchRequest search) {
        ListingSearchRequest criteria = search == null ? new ListingSearchRequest() : search;
        return listingRepository.findAll(ListingSpecifications.activeSearch(criteria)).stream()
                .map(listing -> toDto(listing, null))
                .toList();
    }

    /**
     * Retrieves all listings belonging to the currently authenticated user.
     * <p>
     * Returns all listings where the current user is the seller,
     * regardless of their status (PENDING, ACTIVE, REJECTED, SOLD).
     * </p>
     *
     * @return a list of {@link ListingDto} objects representing the user's own listings
     * @throws BusinessException if the user is not authenticated
     */
    public List<ListingDto> getMyListings() {
        User current = userService.getCurrentUser();
        return listingRepository.findBySellerId(current.getId()).stream()
                .map(listing -> toDto(listing, current.getId()))
                .toList();
    }

    /**
     * Retrieves all listings pending administrative review.
     * <p>
     * Returns listings with status {@code PENDING}. This endpoint is used
     * by administrators to review new listings before they become active.
     * </p>
     *
     * @return a list of {@link ListingDto} objects representing pending listings
     */
    public List<ListingDto> getPendingListings() {
        return listingRepository.findByStatus(ListingStatus.PENDING).stream()
                .map(listing -> toDto(listing, null))
                .toList();
    }

    /**
     * Retrieves a specific listing by its unique identifier.
     * <p>
     * Access restrictions apply: only ACTIVE listings are visible to anonymous users,
     * while owners and admins can view listings in any status.
     * </p>
     *
     * @param id the unique identifier of the listing
     * @return a {@link ListingDto} containing the full listing details
     * @throws BusinessException if the listing does not exist or access is denied
     */
    public ListingDto getById(Long id) {
        Listing listing = getListing(id);
        User current = null;
        try {
            current = userService.getCurrentUser();
        } catch (BusinessException ignored) {
            // anonymous access allowed for active listings
        }
        if (listing.getStatus() != ListingStatus.ACTIVE) {
            boolean allowed = current != null && (listing.getSeller().getId().equals(current.getId())
                    || current.getRole().name().equals("ADMIN"));
            if (!allowed) {
                throw new BusinessException("Listing is not available");
            }
        }
        Long userId = current != null ? current.getId() : null;
        return toDto(listing, userId);
    }

    /**
     * Creates a new listing.
     * <p>
     * The authenticated user becomes the seller. The listing is initially saved
     * with status {@code PENDING} and must be reviewed by an admin before becoming active.
     * Images are uploaded and associated with the listing.
     * </p>
     *
     * @param dto    the listing data (title, description, price, category, city, etc.)
     * @param images optional list of image files to upload
     * @return the created {@link ListingDto} with generated ID and status PENDING
     * @throws BusinessException if validation fails, if the category or city does not exist,
     *         or if the user is not authenticated
     */
    @Transactional
    public ListingDto create(ListingDto dto, List<MultipartFile> images) {
        User seller = userService.getCurrentUser();
        Category category = categoryService.getById(dto.getCategoryId());
        City city = cityService.getById(dto.getCityId());

        Listing listing = new Listing();
        listing.setTitle(dto.getTitle());
        listing.setDescription(dto.getDescription());
        listing.setPrice(dto.getPrice());
        listing.setSpecifications(dto.getSpecifications());
        listing.setSeller(seller);
        listing.setCategory(category);
        listing.setCity(city);
        listing.setStatus(ListingStatus.PENDING);
        listing = listingRepository.save(listing);

        saveImages(listing, images);
        return toDto(listingRepository.findById(listing.getId()).orElseThrow(), seller.getId());
    }

    /**
     * Updates an existing listing.
     * <p>
     * Only the seller can edit their own listings. If the listing was previously
     * rejected, its status is reset to {@code PENDING} for re-review.
     * SOLD listings cannot be edited. New images can be added during update.
     * </p>
     *
     * @param id     the unique identifier of the listing to update
     * @param dto    the updated listing data
     * @param images optional list of new image files to add
     * @return the updated {@link ListingDto}
     * @throws BusinessException if the listing does not exist, if the user is not the seller,
     *         if the listing is SOLD, or if validation fails
     */
    @Transactional
    public ListingDto update(Long id, ListingDto dto, List<MultipartFile> images) {
        Listing listing = getListing(id);
        User current = userService.getCurrentUser();
        if (!listing.getSeller().getId().equals(current.getId())) {
            throw new BusinessException("You can only edit your own listings");
        }
        if (listing.getStatus() == ListingStatus.SOLD) {
            throw new BusinessException("Sold listings cannot be edited");
        }

        listing.setTitle(dto.getTitle());
        listing.setDescription(dto.getDescription());
        listing.setPrice(dto.getPrice());
        listing.setSpecifications(dto.getSpecifications());
        listing.setCategory(categoryService.getById(dto.getCategoryId()));
        listing.setCity(cityService.getById(dto.getCityId()));
        if (listing.getStatus() == ListingStatus.REJECTED) {
            listing.setStatus(ListingStatus.PENDING);
        }
        listingRepository.save(listing);
        if (images != null && !images.isEmpty()) {
            saveImages(listing, images);
        }
        return toDto(listing, current.getId());
    }

    /**
     * Deletes a listing.
     * <p>
     * Only the seller or an admin can delete a listing. This is a hard delete
     * and permanently removes the listing from the system.
     * </p>
     *
     * @param id the unique identifier of the listing to delete
     * @throws BusinessException if the listing does not exist or the user is not authorized
     */
    @Transactional
    public void delete(Long id) {
        Listing listing = getListing(id);
        User current = userService.getCurrentUser();
        boolean isOwner = listing.getSeller().getId().equals(current.getId());
        boolean isAdmin = current.getRole().name().equals("ADMIN");
        if (!isOwner && !isAdmin) {
            throw new BusinessException("Not allowed to delete this listing");
        }
        listingRepository.delete(listing);
    }

    /**
     * Marks a listing as sold.
     * <p>
     * Only the seller can mark their own listing as sold. The status changes
     * to {@code SOLD}, which prevents further edits and hides the listing from
     * public search results.
     * </p>
     *
     * @param id the unique identifier of the listing to mark as sold
     * @return the updated {@link ListingDto} with status SOLD
     * @throws BusinessException if the listing does not exist, if the user is not the seller,
     *         or if the listing is not in a valid state
     */
    @Transactional
    public ListingDto markSold(Long id) {
        Listing listing = getListing(id);
        User current = userService.getCurrentUser();
        if (!listing.getSeller().getId().equals(current.getId())) {
            throw new BusinessException("You can only mark your own listings as sold");
        }
        listing.setStatus(ListingStatus.SOLD);
        return toDto(listingRepository.save(listing), current.getId());
    }

    /**
     * Reviews a pending listing by an administrator.
     * <p>
     * The admin can approve or reject the listing. The listing's status is updated
     * accordingly, and an {@link AdminReview} record is created for audit purposes.
     * </p>
     *
     * @param id      the unique identifier of the listing to review
     * @param request the review request containing the decision and optional comment
     * @return the updated {@link ListingDto}
     * @throws BusinessException if the listing does not exist or is not in PENDING status
     */
    @Transactional
    public ListingDto reviewListing(Long id, AdminReviewRequest request) {
        Listing listing = getListing(id);
        if (listing.getStatus() != ListingStatus.PENDING) {
            throw new BusinessException("Only pending listings can be reviewed");
        }
        User admin = userService.getCurrentUser();
        listing.setStatus(request.getDecision() == ReviewDecision.APPROVED
                ? ListingStatus.ACTIVE
                : ListingStatus.REJECTED);
        listingRepository.save(listing);

        AdminReview review = new AdminReview();
        review.setListing(listing);
        review.setAdmin(admin);
        review.setDecision(request.getDecision());
        review.setComment(request.getComment());
        adminReviewRepository.save(review);

        return toDto(listing, admin.getId());
    }

    /**
     * Saves images associated with a listing.
     * <p>
     * Each image is stored using the {@link FileStorageService} and an entity
     * is created with the correct sort order. Images are ordered sequentially
     * based on the current count.
     * </p>
     *
     * @param listing the listing to associate images with
     * @param images  the list of image files to save
     */
    private void saveImages(Listing listing, List<MultipartFile> images) {
        if (images == null) {
            return;
        }
        int order = listingImageRepository.findByListingIdOrderBySortOrderAsc(listing.getId()).size();
        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                String filename = fileStorageService.store(image);
                ListingImage listingImage = new ListingImage();
                listingImage.setListing(listing);
                listingImage.setFilePath(filename);
                listingImage.setSortOrder(order++);
                listingImageRepository.save(listingImage);
            }
        }
    }

    /**
     * Retrieves a listing by its ID.
     *
     * @param id the unique identifier of the listing
     * @return the {@link Listing} entity
     * @throws BusinessException if the listing does not exist
     */
    private Listing getListing(Long id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Listing not found"));
    }

    /**
     * Converts a Listing entity to a ListingDto.
     * <p>
     * Maps all listing fields, image URLs, favorite status, and seller rating summary
     * to a DTO for safe data transfer to the frontend. The favorite status is only
     * included if a current user ID is provided.
     * </p>
     *
     * @param listing        the Listing entity to convert
     * @param currentUserId  the ID of the currently authenticated user (may be null)
     * @return the corresponding {@link ListingDto}
     */
    public ListingDto toDto(Listing listing, Long currentUserId) {
        ListingDto dto = new ListingDto();
        dto.setId(listing.getId());
        dto.setTitle(listing.getTitle());
        dto.setDescription(listing.getDescription());
        dto.setPrice(listing.getPrice());
        dto.setStatus(listing.getStatus());
        dto.setSpecifications(listing.getSpecifications());
        dto.setSellerId(listing.getSeller().getId());
        dto.setSellerUsername(listing.getSeller().getUsername());
        dto.setCategoryId(listing.getCategory().getId());
        dto.setCategoryName(listing.getCategory().getName());
        dto.setCityId(listing.getCity().getId());
        dto.setCityName(listing.getCity().getName());
        dto.setCreatedAt(listing.getCreatedAt());
        dto.setUpdatedAt(listing.getUpdatedAt());

        // Add seller rating summary
        SellerRatingSummaryDto summary = ratingService.getRatingSummary(listing.getSeller().getId());
        dto.setAverageRating(summary.getAverage());
        dto.setRatingCount(summary.getCount());

        // Add image URLs
        List<String> urls = new ArrayList<>();
        for (ListingImage image : listingImageRepository.findByListingIdOrderBySortOrderAsc(listing.getId())) {
            urls.add(fileStorageService.toPublicUrl(image.getFilePath()));
        }
        dto.setImageUrls(urls);

        // Add favorite status if user is authenticated
        if (currentUserId != null) {
            dto.setFavorite(favoriteRepository.existsByUserIdAndListingId(currentUserId, listing.getId()));
        }

        return dto;
    }
}