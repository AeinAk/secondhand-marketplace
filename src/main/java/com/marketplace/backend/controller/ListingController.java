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

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping("/active")
    public List<ListingDto> getActiveListings() {
        return listingService.getActiveListings(new ListingSearchRequest());
    }

    @PostMapping("/search")
    public List<ListingDto> search(@RequestBody ListingSearchRequest request) {
        return listingService.getActiveListings(request);
    }

    @GetMapping("/mine")
    public List<ListingDto> getMyListings() {
        return listingService.getMyListings();
    }

    @GetMapping("/{id}")
    public ListingDto getById(@PathVariable Long id) {
        return listingService.getById(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ListingDto create(@RequestPart("listing") @Valid ListingDto listing,
                             @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return listingService.create(listing, images == null ? Collections.emptyList() : images);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ListingDto update(@PathVariable Long id,
                             @RequestPart("listing") @Valid ListingDto listing,
                             @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return listingService.update(id, listing, images);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        listingService.delete(id);
    }

    @PutMapping("/{id}/sold")
    public ListingDto markSold(@PathVariable Long id) {
        return listingService.markSold(id);
    }
}
