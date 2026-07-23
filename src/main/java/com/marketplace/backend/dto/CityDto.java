package com.marketplace.backend.dto;

/**
 * Data Transfer Object for city information.
 * <p>
 * Represents a city with its associated province. Used in the marketplace
 * for location-based filtering of listings and displaying user locations.
 * This DTO is used in dropdown selection components in the frontend.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class CityDto {

    /**
     * The unique identifier of the city.
     */
    private Long id;

    /**
     * The display name of the city.
     * <p>
     * This name must be unique across all cities and is used for display
     * in user interfaces and dropdown lists.
     * </p>
     */
    private String name;

    /**
     * The province or state that the city belongs to.
     * <p>
     * Provides additional geographic context for the city.
     * </p>
     */
    private String province;

    /**
     * Returns the unique identifier of the city.
     *
     * @return the city ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the city.
     *
     * @param id the city ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the display name of the city.
     *
     * @return the city name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of the city.
     *
     * @param name the city name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the province that the city belongs to.
     *
     * @return the province name
     */
    public String getProvince() {
        return province;
    }

    /**
     * Sets the province that the city belongs to.
     *
     * @param province the province name to set
     */
    public void setProvince(String province) {
        this.province = province;
    }

    /**
     * Returns the city name as its string representation.
     * <p>
     * This method is overridden to provide a human-readable representation
     * of the city, which is particularly useful for display in ComboBox
     * and other UI components in the frontend.
     * </p>
     *
     * @return the city name
     */
    @Override
    public String toString() {
        return name;
    }
}