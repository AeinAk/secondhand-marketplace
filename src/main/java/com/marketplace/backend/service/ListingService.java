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

    public List<ListingDto> getActiveListings(ListingSearchRequest search) {
        ListingSearchRequest criteria = search == null ? new ListingSearchRequest() : search;
        return listingRepository.findAll(ListingSpecifications.activeSearch(criteria)).stream()
                .map(listing -> toDto(listing, null))
                .toList();
    }

    public List<ListingDto> getMyListings() {
        User current = userService.getCurrentUser();
        return listingRepository.findBySellerId(current.getId()).stream()
                .map(listing -> toDto(listing, current.getId()))
                .toList();
    }

    public List<ListingDto> getPendingListings() {
        return listingRepository.findByStatus(ListingStatus.PENDING).stream()
                .map(listing -> toDto(listing, null))
                .toList();
    }

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

    private Listing getListing(Long id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Listing not found"));
    }

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

        SellerRatingSummaryDto summary = ratingService.getRatingSummary(listing.getSeller().getId());
        dto.setAverageRating(summary.getAverage());
        dto.setRatingCount(summary.getCount());


        List<String> urls = new ArrayList<>();
        for (ListingImage image : listingImageRepository.findByListingIdOrderBySortOrderAsc(listing.getId())) {
            urls.add(fileStorageService.toPublicUrl(image.getFilePath()));
        }
        dto.setImageUrls(urls);

        if (currentUserId != null) {
            dto.setFavorite(favoriteRepository.existsByUserIdAndListingId(currentUserId, listing.getId()));
        }


        return dto;
    }
}
