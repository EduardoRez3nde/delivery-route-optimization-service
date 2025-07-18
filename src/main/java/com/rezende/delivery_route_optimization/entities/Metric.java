package com.rezende.delivery_route_optimization.entities;

public class Metric {

    private Double distanceTotalKm;
    private Long timeTotalMinutes;
    private String unitDistance;
    private String unitTime;

    public Metric() { }

    private Metric(
            final Double distanceTotalKm,
            final Long timeTotalMinutes,
            final String unitDistance,
            final String unitTime
    ) {
        this.distanceTotalKm = distanceTotalKm;
        this.timeTotalMinutes = timeTotalMinutes;
        this.unitDistance = unitDistance;
        this.unitTime = unitTime;
    }

    public static Metric from(
            final Double distanceTotalKm,
            final Long timeTotalMinutes,
            final String unitDistance,
            final String unitTime
    ) {
        return new Metric(distanceTotalKm, timeTotalMinutes, unitDistance, unitTime);
    }

    public Double getDistanceTotalKm() {
        return distanceTotalKm;
    }

    public void setDistanceTotalKm(Double distanceTotalKm) {
        this.distanceTotalKm = distanceTotalKm;
    }

    public Long getTimeTotalMinutes() {
        return timeTotalMinutes;
    }

    public void setTimeTotalMinutes(Long timeTotalMinutes) {
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

    @Override
    public String toString() {
        return "Metric{" +
                "distanceTotalKm=" + distanceTotalKm +
                ", timeTotalMinutes=" + timeTotalMinutes +
                ", unitDistance='" + unitDistance + '\'' +
                ", unitTime='" + unitTime + '\'' +
                '}';
    }
}
