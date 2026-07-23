package com.marketplace.backend.dto;

/**
 * Data Transfer Object for seller rating summary statistics.
 * <p>
 * Encapsulates aggregated rating data for a seller, including the average
 * rating score and the total number of ratings received. This DTO is used
 * to provide a quick overview of a seller's reputation without requiring
 * the full list of individual ratings.
 * </p>
 *
 * @author Atbin Jafarzadeh Afshari
 * @version 1.0
 * @since 1.0
 */
public class SellerRatingSummaryDto {

    /**
     * The average rating score of the seller.
     * <p>
     * Calculated as the arithmetic mean of all ratings received by the seller.
     * Value is between 0.0 and 5.0, where 0.0 indicates no ratings yet.
     * </p>
     */
    private double average;

    /**
     * The total number of ratings the seller has received.
     */
    private long count;

    /**
     * Constructs a SellerRatingSummaryDto with the given average and count.
     *
     * @param average the average rating score (0.0–5.0)
     * @param count   the total number of ratings received
     */
    public SellerRatingSummaryDto(double average, long count) {
        this.average = average;
        this.count = count;
    }

    /**
     * Returns the average rating score.
     *
     * @return the average rating (0.0–5.0)
     */
    public double getAverage() {
        return average;
    }

    /**
     * Sets the average rating score.
     *
     * @param average the average to set (0.0–5.0)
     */
    public void setAverage(double average) {
        this.average = average;
    }

    /**
     * Returns the total number of ratings.
     *
     * @return the rating count
     */
    public long getCount() {
        return count;
    }

    /**
     * Sets the total number of ratings.
     *
     * @param count the count to set
     */
    public void setCount(long count) {
        this.count = count;
    }
}