package com.rezende.delivery_route_optimization.dto;

public class MetricDTO {

    private Double distanceTotalKm;
    private Double timeTotalMinutes;
    private String unitDistance;
    private String unitTime;

    public MetricDTO() { }

    public MetricDTO(
            final Double distanceTotalKm,
            final Double timeTotalMinutes,
            final String unitDistance,
            final String unitTime
    ) {
        this.distanceTotalKm = distanceTotalKm;
        this.timeTotalMinutes = timeTotalMinutes;
        this.unitDistance = unitDistance;
        this.unitTime = unitTime;
    }

    public Double getDistanceTotalKm() {
        return distanceTotalKm;
    }

    public void setDistanceTotalKm(Double distanceTotalKm) {
        this.distanceTotalKm = distanceTotalKm;
    }

    public Double getTimeTotalMinutes() {
        return timeTotalMinutes;
    }

    public void setTimeTotalMinutes(Double timeTotalMinutes) {
        this.timeTotalMinutes = timeTotalMinutes;
    }

    public String getUnitDistance() {
        return unitDistance;
    }

    public void setUnitDistance(String unitDistance) {
        this.unitDistance = unitDistance;
    }

    public String getUnitTime() {
        return unitTime;
    }

    public void setUnitTime(String unitTime) {
        this.unitTime = unitTime;
    }
}
