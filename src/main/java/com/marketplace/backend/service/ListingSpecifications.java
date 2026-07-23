package com.marketplace.backend.service;

import com.marketplace.backend.dto.ListingSearchRequest;
import com.marketplace.backend.entity.Listing;
import com.marketplace.backend.entity.ListingStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for building JPA Specifications for advanced listing searches.
 * <p>
 * This class provides a single method {@link #activeSearch(ListingSearchRequest)}
 * that constructs dynamic WHERE clauses based on the provided search criteria.
 * All conditions are combined with AND logic, and the search is restricted to
 * listings with status {@code ACTIVE} to ensure only available listings are returned.
 * </p>
 * <p>
 * The specifications are used by the {@link ListingService} to perform filtered
 * queries without writing complex JPQL or native SQL. This approach leverages
 * Spring Data JPA's Specification API for type-safe, dynamic query building.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public final class ListingSpecifications {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ListingSpecifications() {
    }

    /**
     * Creates a Specification for searching active listings with the given filters.
     * <p>
     * The Specification applies the following conditions:
     * <ul>
     *   <li>Only listings with status {@code ACTIVE} are included.</li>
     *   <li>If {@code keyword} is provided, it performs a case-insensitive partial
     *       search in both the title and description fields.</li>
     *   <li>If {@code categoryId} is provided, it filters by the category ID.</li>
     *   <li>If {@code cityId} is provided, it filters by the city ID.</li>
     *   <li>If {@code minPrice} is provided, it filters listings with price
     *       greater than or equal to the value.</li>
     *   <li>If {@code maxPrice} is provided, it filters listings with price
     *       less than or equal to the value.</li>
     *   <li>If {@code specifications} is provided, it performs a case-insensitive
     *       partial search in the specifications field.</li>
     * </ul>
     * All conditions are combined with AND logic. If a filter is not provided,
     * it is simply ignored.
     * </p>
     *
     * @param request the search request containing optional filter criteria
     * @return a JPA {@link Specification} that can be used with
     *         {@link org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(Specification)}
     * @throws NullPointerException if {@code request} is null
     */
    public static Specification<Listing> activeSearch(ListingSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), ListingStatus.ACTIVE));

            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                String pattern = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }
            if (request.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), request.getCategoryId()));
            }
            if (request.getCityId() != null) {
                predicates.add(cb.equal(root.get("city").get("id"), request.getCityId()));
            }
            if (request.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
            }
            if (request.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
            }
            if (request.getSpecifications() != null && !request.getSpecifications().isBlank()) {
                String pattern = "%" + request.getSpecifications().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("specifications")), pattern));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}