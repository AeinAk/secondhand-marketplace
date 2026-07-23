package com.marketplace.backend.service;

import com.marketplace.backend.dto.CityDto;
import com.marketplace.backend.entity.City;
import com.marketplace.backend.exception.BusinessException;
import com.marketplace.backend.repository.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing cities.
 * <p>
 * Provides business logic for retrieving and creating cities, as well as
 * fetching individual cities by ID. Cities are used for location-based
 * filtering of listings and for displaying seller locations.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Service
public class CityService {

    private final CityRepository cityRepository;

    /**
     * Constructs a CityService with the required repository.
     *
     * @param cityRepository the repository for city data access
     */
    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    /**
     * Retrieves all cities.
     * <p>
     * Returns a list of all cities in the system, mapped to DTO objects.
     * </p>
     *
     * @return a list of {@link CityDto} representing all cities
     */
    public List<CityDto> getAll() {
        return cityRepository.findAll().stream().map(this::toDto).toList();
    }

    /**
     * Creates a new city.
     * <p>
     * Validates that the city name is unique. If the name already exists,
     * a {@link BusinessException} is thrown. The city is then saved with
     * the provided name and province.
     * </p>
     *
     * @param name     the name of the new city (must be unique)
     * @param province the province or state the city belongs to
     * @return the created {@link CityDto}
     * @throws BusinessException if a city with the same name already exists
     */
    @Transactional
    public CityDto create(String name, String province) {
        if (cityRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new BusinessException("City already exists");
        }
        City city = new City();
        city.setName(name);
        city.setProvince(province);
        return toDto(cityRepository.save(city));
    }

    /**
     * Retrieves a city by its ID.
     * <p>
     * This method is used internally to fetch city entities for other
     * service operations. If the city does not exist, a
     * {@link BusinessException} is thrown.
     * </p>
     *
     * @param id the unique identifier of the city
     * @return the {@link City} entity
     * @throws BusinessException if the city does not exist
     */
    public City getById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new BusinessException("City not found"));
    }

    /**
     * Converts a City entity to a CityDto.
     * <p>
     * This method maps the entity fields to a DTO for safe data transfer
     * to the frontend, excluding any internal JPA-related fields.
     * </p>
     *
     * @param city the City entity to convert
     * @return the corresponding {@link CityDto}
     */
    public CityDto toDto(City city) {
        CityDto dto = new CityDto();
        dto.setId(city.getId());
        dto.setName(city.getName());
        dto.setProvince(city.getProvince());
        return dto;
    }
}