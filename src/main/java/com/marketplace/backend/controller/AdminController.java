package com.marketplace.backend.controller;

import com.marketplace.backend.dto.AdminReviewRequest;
import com.marketplace.backend.dto.ListingDto;
import com.marketplace.backend.dto.UserDto;
import com.marketplace.backend.service.ListingService;
import com.marketplace.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ListingService listingService;
    private final UserService userService;

    public AdminController(ListingService listingService, UserService userService) {
        this.listingService = listingService;
        this.userService = userService;
    }

    @GetMapping("/listings/pending")
    public List<ListingDto> pendingListings() {
        return listingService.getPendingListings();
    }

    @PostMapping("/listings/{id}/review")
    public ListingDto reviewListing(@PathVariable Long id, @Valid @RequestBody AdminReviewRequest request) {
        return listingService.reviewListing(id, request);
    }

    @DeleteMapping("/listings/{id}")
    public void deleteListing(@PathVariable Long id) {
        listingService.delete(id);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/users/{id}/block")
    public UserDto blockUser(@PathVariable Long id, @RequestParam boolean blocked) {
        return userService.blockUser(id, blocked);
    }
}
