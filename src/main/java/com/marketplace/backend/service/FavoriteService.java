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

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ListingRepository listingRepository;
    private final ListingService listingService;
    private final UserService userService;

    public FavoriteService(FavoriteRepository favoriteRepository,
                           ListingRepository listingRepository,
                           ListingService listingService,
                           UserService userService) {
        this.favoriteRepository = favoriteRepository;
        this.listingRepository = listingRepository;
        this.listingService = listingService;
        this.userService = userService;
    }

    public List<ListingDto> getFavorites() {
        User user = userService.getCurrentUser();
        return favoriteRepository.findByUserId(user.getId()).stream()
                .map(Favorite::getListing)
                .map(listing -> listingService.toDto(listing, user.getId()))
                .toList();
    }

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
