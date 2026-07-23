package com.marketplace.backend.dto;

import com.marketplace.backend.entity.ListingStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for listing (advertisement) details.
 * <p>
 * Encapsulates all information about a listing, including its metadata,
 * seller information, category, city, images, and status. This DTO is used
 * for transferring listing data between the backend and frontend, and for
 * validation during creation and update operations.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class ListingDto {

    /**
     * The unique identifier of the listing.
     */
    private Long id;

    /**
     * The title of the listing.
     * <p>
     * Must not be blank and cannot exceed 150 characters.
     * </p>
     */
    @NotBlank
    @Size(max = 150)
    private String title;

    /**
     * The detailed description of the listing.
     * <p>
     * Must not be blank and cannot exceed 2000 characters.
     * </p>
     */
    @NotBlank
    @Size(max = 2000)
    private String description;

    /**
     * The price of the item or service being offered.
     * <p>
     * Must be a non-negative decimal value (minimum 0.0).
     * </p>
     */
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal price;

    /**
     * The current status of the listing.
     * <p>
     * Possible values: PENDING, ACTIVE, REJECTED, SOLD.
     * </p>
     */
    private ListingStatus status;

    /**
     * Optional specifications or additional details about the item.
     * <p>
     * Examples: color, size, model, condition, etc.
     * </p>
     */
    private String specifications;

    /**
     * The unique identifier of the seller (owner) of the listing.
     */
    private Long sellerId;

    /**
     * The username of the seller.
     */
    private String sellerUsername;

    /**
     * The unique identifier of the category to which the listing belongs.
     */
    private Long categoryId;

    /**
     * The name of the category.
     */
    private String categoryName;

    /**
     * The unique identifier of the city where the listing is located.
     */
    private Long cityId;

    /**
     * The name of the city.
     */
    private String cityName;

    /**
     * List of URLs to the images associated with the listing.
     * <p>
     * Each URL points to an uploaded image file served by the backend.
     * May be empty if no images are attached.
     * </p>
     */
    private List<String> imageUrls = new ArrayList<>();

    /**
     * The timestamp when the listing was created.
     */
    private LocalDateTime createdAt;

    /**
     * The timestamp when the listing was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * Indicates whether the current user has marked this listing as a favorite.
     * <p>
     * Only relevant when the listing is viewed by an authenticated user.
     * </p>
     */
    private boolean favorite;

    /**
     * The average rating score of the seller (calculated from all received ratings).
     * <p>
     * Value is between 0.0 and 5.0, where 0.0 indicates no ratings yet.
     * </p>
     */
    private double averageRating;

    /**
     * The total number of ratings the seller has received.
     */
    private long ratingCount;

    /**
     * Returns the average rating of the seller.
     *
     * @return the average rating (0.0–5.0)
     */
    public double getAverageRating() {
        return averageRating;
    }

    /**
     * Sets the average rating of the seller.
     *
     * @param averageRating the average rating to set
     */
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Returns the total number of ratings the seller has received.
     *
     * @return the rating count
     */
    public long getRatingCount() {
        return ratingCount;
    }

    /**
     * Sets the total number of ratings the seller has received.
     *
     * @param ratingCount the rating count to set
     */
    public void setRatingCount(long ratingCount) {
        this.ratingCount = ratingCount;
    }

    /**
     * Returns the unique identifier of the listing.
     *
     * @return the listing ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the listing.
     *
     * @param id the listing ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the title of the listing.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the listing.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the description of the listing.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the listing.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the price of the listing.
     *
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the price of the listing.
     *
     * @param price the price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Returns the current status of the listing.
     *
     * @return the {@link ListingStatus}
     */
    public ListingStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of the listing.
     *
     * @param status the status to set
     */
    public void setStatus(ListingStatus status) {
        this.status = status;
    }

    /**
     * Returns the optional specifications of the listing.
     *
     * @return the specifications string, or null if not set
     */
    public String getSpecifications() {
        return specifications;
    }

    /**
     * Sets the optional specifications of the listing.
     *
     * @param specifications the specifications to set
     */
    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    /**
     * Returns the seller's user ID.
     *
     * @return the seller ID
     */
    public Long getSellerId() {
        return sellerId;
    }

    /**
     * Sets the seller's user ID.
     *
     * @param sellerId the seller ID to set
     */
    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    /**
     * Returns the seller's username.
     *
     * @return the seller username
     */
    public String getSellerUsername() {
        return sellerUsername;
    }

    /**
     * Sets the seller's username.
     *
     * @param sellerUsername the seller username to set
     */
    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }

    /**
     * Returns the category ID of the listing.
     *
     * @return the category ID
     */
    public Long getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the category ID of the listing.
     *
     * @param categoryId the category ID to set
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Returns the category name of the listing.
     *
     * @return the category name
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * Sets the category name of the listing.
     *
     * @param categoryName the category name to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * Returns the city ID of the listing.
     *
     * @return the city ID
     */
    public Long getCityId() {
        return cityId;
    }

    /**
     * Sets the city ID of the listing.
     *
     * @param cityId the city ID to set
     */
    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    /**
     * Returns the city name of the listing.
     *
     * @return the city name
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * Sets the city name of the listing.
     *
     * @param cityName the city name to set
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     * Returns the list of image URLs associated with the listing.
     *
     * @return the list of image URLs (may be empty)
     */
    public List<String> getImageUrls() {
        return imageUrls;
    }

    /**
     * Sets the list of image URLs associated with the listing.
     *
     * @param imageUrls the list of image URLs to set
     */
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    /**
     * Returns the creation timestamp of the listing.
     *
     * @return the creation time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the listing.
     *
     * @param createdAt the creation time to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the last update timestamp of the listing.
     *
     * @return the update time
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp of the listing.
     *
     * @param updatedAt the update time to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Checks whether the current user has marked this listing as a favorite.
     *
     * @return {@code true} if the listing is favorited, {@code false} otherwise
     */
    public boolean isFavorite() {
        return favorite;
    }

    /**
     * Sets whether the current user has marked this listing as a favorite.
     *
     * @param favorite {@code true} to mark as favorite, {@code false} to unmark
     */
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}