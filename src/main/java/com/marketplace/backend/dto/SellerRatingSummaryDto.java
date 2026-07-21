package com.marketplace.backend.dto;

public class SellerRatingSummaryDto {
    private double average;
    private long count;

    public SellerRatingSummaryDto(double average, long count) {
        this.average = average;
        this.count = count;
    }

    public double getAverage() { return average; }
    public void setAverage(double average) { this.average = average; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}