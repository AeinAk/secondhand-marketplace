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

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public SellerRatingDto rateSeller(@Valid @RequestBody RatingRequest request) {
        return ratingService.rateSeller(request);
    }

    @GetMapping("/seller/{sellerId}")
    public List<SellerRatingDto> getSellerRatings(@PathVariable Long sellerId) {
        return ratingService.getSellerRatings(sellerId);
    }
}
