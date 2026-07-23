package com.marketplace.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entity representing a product category.
 * <p>
 * Categories are used to classify listings in the marketplace, enabling
 * users to browse and filter items by type. Each category has a unique name
 * and an optional description that provides more context about the category.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Category {

    /**
     * The unique identifier of the category.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The display name of the category.
     * <p>
     * This name must be unique across all categories and is used for display
     * in user interfaces and dropdown lists. Cannot be null and has a maximum
     * length of 80 characters.
     * </p>
     */
    @Column(nullable = false, length = 80)
    private String name;

    /**
     * An optional detailed description of the category.
     * <p>
     * Provides additional context about what types of listings belong
     * to this category. Maximum length is 255 characters.
     * </p>
     */
    @Column(length = 255)
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
}