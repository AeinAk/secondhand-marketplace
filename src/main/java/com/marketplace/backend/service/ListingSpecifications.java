package com.marketplace.backend.service;

import com.marketplace.backend.dto.ListingSearchRequest;
import com.marketplace.backend.entity.Listing;
import com.marketplace.backend.entity.ListingStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ListingSpecifications {

    private ListingSpecifications() {
    }

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
