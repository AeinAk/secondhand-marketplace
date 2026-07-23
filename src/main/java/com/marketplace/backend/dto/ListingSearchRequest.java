package com.marketplace.backend.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object for advanced listing search requests.
 * <p>
 * Encapsulates search criteria for filtering listings. All fields are optional,
 * and only provided fields are applied as filters. The search is performed
 * on ACTIVE listings only, combining all specified criteria with AND logic.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class ListingSearchRequest {

    /**
     * A keyword to search for in the listing title and description.
     * <p>
     * The search is case-insensitive and matches partial words.
     * If provided, listings containing the keyword in their title or
     * description will be included.
     * </p>
     */
    private String keyword;

    /**
     * The unique identifier of the category to filter by.
     * <p>
     * If provided, only listings belonging to this category will be included.
     * </p>
     */
    private Long categoryId;

    /**
     * The unique identifier of the city to filter by.
     * <p>
     * If provided, only listings located in this city will be included.
     * </p>
     */
    private Long cityId;

    /**
     * The minimum price threshold for filtering listings.
     * <p>
     * If provided, only listings with a price greater than or equal
     * to this value will be included. Must be non-negative.
     * </p>
     */
    private BigDecimal minPrice;

    /**
     * The maximum price threshold for filtering listings.
     * <p>
     * If provided, only listings with a price less than or equal
     * to this value will be included. Must be non-negative.
     * </p>
     */
    private BigDecimal maxPrice;

    /**
     * A keyword to search for in the listing specifications field.
     * <p>
     * The search is case-insensitive and matches partial text.
     * If provided, listings containing the keyword in their
     * specifications will be included.
     * </p>
     */
    private String specifications;

    /**
     * Returns the keyword for searching in title and description.
     *
     * @return the keyword string, or {@code null} if not set
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Sets the keyword for searching in title and description.
     *
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the category ID filter.
     *
     * @return the category ID, or {@code null} if not set
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the category ID filter.
     *
     * @param categoryId the category ID to set
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Returns the city ID filter.
     *
     * @return the city ID, or {@code null} if not set
     */
    public Long getCityId() {
        return cityId;
    }

    /**
     * Sets the city ID filter.
     *
     * @param cityId the city ID to set
     */
    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    /**
     * Returns the minimum price filter.
     *
     * @return the minimum price, or {@code null} if not set
     */
    public BigDecimal getMinPrice() {
        return minPrice;
    }

    /**
     * Sets the minimum price filter.
     *
     * @param minPrice the minimum price to set
     */
    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    /**
     * Returns the maximum price filter.
     *
     * @return the maximum price, or {@code null} if not set
     */
    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    /**
     * Sets the maximum price filter.
     *
     * @param maxPrice the maximum price to set
     */
    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    /**
     * Returns the keyword for searching in specifications.
     *
     * @return the specifications keyword, or {@code null} if not set
     */
    public String getSpecifications() {
        return specifications;
    }

    /**
     * Sets the keyword for searching in specifications.
     *
     * @param specifications the specifications keyword to set
     */
    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }
}