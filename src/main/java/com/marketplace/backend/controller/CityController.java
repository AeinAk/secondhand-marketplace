package com.marketplace.backend.controller;

import com.marketplace.backend.dto.CityDto;
import com.marketplace.backend.service.CityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing cities.
 * <p>
 * Provides endpoints for retrieving all cities and creating new cities.
 * City data is used for location-based filtering of listings.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final CityService cityService;

    /**
     * Constructs a {@code CityController} with the required city service.
     *
     * @param cityService the service handling city business logic
     */
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    /**
     * Retrieves all available cities.
     *
     * @return a list of {@link CityDto} objects representing all cities
     */
    @GetMapping
    public List<CityDto> getAll() {
        return cityService.getAll();
    }

    /**
     * Creates a new city.
     * <p>
     * Expects a JSON object containing the city name and the province it belongs to.
     * The city name must be unique across the system.
     * </p>
     *
     * @param body the request body containing {@code "name"} and {@code "province"} fields
     * @return the created {@link CityDto} with its generated ID
     * @throws com.marketplace.backend.exception.BusinessException if a city with the same name already exists
     */
    @PostMapping
    public CityDto create(@RequestBody Map<String, String> body) {
        return cityService.create(body.get("name"), body.get("province"));
    }
}