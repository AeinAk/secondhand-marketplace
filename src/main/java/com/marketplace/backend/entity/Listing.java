package com.marketplace.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a listing (advertisement) in the marketplace.
 * <p>
 * A listing is the core entity of the system, representing an item or service
 * offered for sale by a user. It contains comprehensive information including
 * the item's description, price, category, location, status, and associated images.
 * Listings go through a lifecycle from creation (PENDING) to approval (ACTIVE)
 * or rejection, and eventually may be marked as SOLD.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "listings")
public class Listing {

    /**
     * The unique identifier of the listing.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The title of the listing.
     * <p>
     * This is a short, descriptive title for the item or service being offered.
     * Cannot be null and has a maximum length of 150 characters.
     * </p>
     */
    @Column(nullable = false, length = 150)
    private String title;

    /**
     * The detailed description of the listing.
     * <p>
     * Provides comprehensive information about the item or service, including
     * condition, features, and any other relevant details. Cannot be null and
     * has a maximum length of 2000 characters.
     * </p>
     */
    @Column(nullable = false, length = 2000)
    private String description;

    /**
     * The price of the item or service being offered.
     * <p>
     * Stored as a BigDecimal with precision 12 and scale 2 to handle monetary
     * values accurately. Cannot be null.
     * </p>
     */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /**
     * The current status of the listing.
     * <p>
     * Possible values: PENDING (awaiting admin review), ACTIVE (approved and visible),
     * REJECTED (rejected by admin), or SOLD (item has been sold). Defaults to PENDING
     * when a new listing is created.
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingStatus status = ListingStatus.PENDING;

    /**
     * Optional specifications or additional details about the item.
     * <p>
     * Can include information like color, size, model, condition, or any other
     * relevant attributes. Maximum length is 500 characters.
     * </p>
     */
    @Column(length = 500)
    private String specifications;

    /**
     * The seller (owner) of the listing.
     * <p>
     * This association is mandatory and loaded lazily for performance.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    /**
     * The category to which this listing belongs.
     * <p>
     * Categories help classify listings for browsing and search. This association
     * is mandatory and loaded lazily for performance.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * The city where the item is located.
     * <p>
     * Used for location-based searching and filtering. This association is mandatory
     * and loaded lazily for performance.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    /**
     * The list of images associated with this listing.
     * <p>
     * Images are stored in a separate entity {@link ListingImage} with a
     * one-to-many relationship. This collection is loaded lazily and can be empty
     * if no images have been uploaded.
     * </p>
     */
    @OneToMany(mappedBy = "listing", fetch = FetchType.LAZY)
    private List<ListingImage> images = new ArrayList<>();

    /**
     * The timestamp when the listing was created.
     * <p>
     * Automatically set before the entity is persisted. Used for sorting and
     * displaying "new" listings.
     * </p>
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the listing was last updated.
     * <p>
     * Automatically updated whenever the entity is modified. Used for tracking
     * when listings were last changed.
     * </p>
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Initializes timestamps before the entity is persisted.
     * <p>
     * Sets both {@code createdAt} and {@code updatedAt} to the current time.
     * This method is automatically called by JPA before the entity is saved.
     * </p>
     */
    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    /**
     * Updates the {@code updatedAt} timestamp before the entity is updated.
     * <p>
     * This method is automatically called by JPA before the entity is updated
     * in the database, ensuring that the update timestamp is always current.
     * </p>
     */
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
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
     * @return the price as {@link BigDecimal}
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
     * Returns the seller of the listing.
     *
     * @return the seller {@link User} entity
     */
    public User getSeller() {
        return seller;
    }

    /**
     * Sets the seller of the listing.
     *
     * @param seller the seller to set
     */
    public void setSeller(User seller) {
        this.seller = seller;
    }

    /**
     * Returns the category of the listing.
     *
     * @return the {@link Category} entity
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Sets the category of the listing.
     *
     * @param category the category to set
     */
    public void setCategory(Category category) {
        this.category = category;
    }

    /**
     * Returns the city of the listing.
     *
     * @return the {@link City} entity
     */
    public City getCity() {
        return city;
    }

    /**
     * Sets the city of the listing.
     *
     * @param city the city to set
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * Returns the list of images associated with the listing.
     *
     * @return the list of {@link ListingImage} entities
     */
    public List<ListingImage> getImages() {
        return images;
    }

    /**
     * Sets the list of images associated with the listing.
     *
     * @param images the list of images to set
     */
    public void setImages(List<ListingImage> images) {
        this.images = images;
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
}