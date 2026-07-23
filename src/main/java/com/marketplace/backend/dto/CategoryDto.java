package com.marketplace.backend.dto;

/**
 * Data Transfer Object for category information.
 * <p>
 * Represents a product category used for classifying listings in the marketplace.
 * Contains basic category metadata including ID, name, and an optional description.
 * This DTO is used for transferring category data between the backend and frontend,
 * and is also used in dropdown selection components.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class CategoryDto {

    /**
     * The unique identifier of the category.
     */
    private Long id;

    /**
     * The display name of the category.
     * <p>
     * This name must be unique across all categories and is used for
     * display in user interfaces and dropdown lists.
     * </p>
     */
    private String name;

    /**
     * An optional detailed description of the category.
     * <p>
     * Provides additional context about what types of listings belong
     * to this category. May be null or empty.
     * </p>
     */
    private String description;

    /**
     * Returns the unique identifier of the category.
     *
     * @return the category ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the category.
     *
     * @param id the category ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the display name of the category.
     *
     * @return the category name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name of the category.
     *
     * @param name the category name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the optional description of the category.
     *
     * @return the category description, or null if none was set
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the optional description of the category.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the category name as its string representation.
     * <p>
     * This method is overridden to provide a human-readable representation
     * of the category, which is particularly useful for display in
     * ComboBox and other UI components in the frontend.
     * </p>
     *
     * @return the category name
     */
    @Override
    public String toString() {
        return name;
    }
}