package com.marketplace.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Entity representing a city with its associated province.
 * <p>
 * Cities are used to indicate the location of listings, enabling
 * users to filter and browse items by geographic area. Each city
 * has a unique name and belongs to a province (state). This data
 * is typically loaded from a reference table and used in dropdown
 * selectors during listing creation and search.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "cities", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class City {

    /**
     * The unique identifier of the city.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The display name of the city.
     * <p>
     * This name must be unique across all cities and is used for display
     * in user interfaces and dropdown lists. Cannot be null and has a maximum
     * length of 80 characters.
     * </p>
     */
    @Column(nullable = false, length = 80)
    private String name;

    /**
     * The province or state that the city belongs to.
     * <p>
     * Provides additional geographic context for the city. This field is optional
     * and can be null, with a maximum length of 80 characters.
     * </p>
     */
    @Column(length = 80)
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
     * @return the province name, or null if not set
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
}