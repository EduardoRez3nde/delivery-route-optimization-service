package com.rezende.delivery_route_optimization.entities;

public class Coordinates {

    private Double latitude;
    private Double Longitude;

    public Coordinates() { }

    private Coordinates(final Double latitude, final Double longitude) {
        this.latitude = latitude;
        this.Longitude = longitude;
    }

    public static Coordinates from(final Double latitude, final Double longitude) {
        return new Coordinates(latitude, longitude);
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(final Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(final Double longitude) {
        Longitude = longitude;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "latitude=" + latitude +
                ", Longitude=" + Longitude +
                '}';
    }
}
