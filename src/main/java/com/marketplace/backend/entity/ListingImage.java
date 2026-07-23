package com.marketplace.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity representing an image associated with a listing.
 * <p>
 * Each listing can have multiple images. This entity stores the file path of the
 * image and its display order within the listing's image gallery. The images
 * are ordered by the {@code sortOrder} field, allowing the seller to control
 * the sequence in which images appear.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "listing_images")
public class ListingImage {

    /**
     * The unique identifier of the listing image record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The listing that this image belongs to.
     * <p>
     * This association is mandatory and loaded lazily for performance.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    /**
     * The file path or URL of the image.
     * <p>
     * This path can be an absolute file system path, a relative path, or a URL
     * depending on the storage strategy. It is stored as a string with a maximum
     * length of 500 characters.
     * </p>
     */
    @Column(nullable = false, length = 500)
    private String filePath;

    /**
     * The display order of the image within the listing's gallery.
     * <p>
     * Images are displayed in ascending order of this field. Default value is 0.
     * </p>
     */
    @Column(nullable = false)
    private int sortOrder = 0;

    /**
     * Returns the unique identifier of the listing image.
     *
     * @return the image ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the listing image.
     *
     * @param id the image ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the listing that this image belongs to.
     *
     * @return the {@link Listing} entity
     */
    public Listing getListing() {
        return listing;
    }

    /**
     * Sets the listing that this image belongs to.
     *
     * @param listing the listing to set
     */
    public void setListing(Listing listing) {
        this.listing = listing;
    }

    /**
     * Returns the file path of the image.
     *
     * @return the file path string
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the file path of the image.
     *
     * @param filePath the file path to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns the display order of the image.
     *
     * @return the sort order
     */
    public int getSortOrder() {
        return sortOrder;
    }

    /**
     * Sets the display order of the image.
     *
     * @param sortOrder the sort order to set
     */
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}