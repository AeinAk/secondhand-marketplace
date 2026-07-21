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

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public List<ListingDto> getFavorites() {
        return favoriteService.getFavorites();
    }

    @PostMapping("/{listingId}")
    public void addFavorite(@PathVariable Long listingId) {
        favoriteService.addFavorite(listingId);
    }

    @DeleteMapping("/{listingId}")
    public void removeFavorite(@PathVariable Long listingId) {
        favoriteService.removeFavorite(listingId);
    }
}
