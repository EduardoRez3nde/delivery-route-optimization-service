package com.rezende.delivery_route_optimization.entities;

public class Coordinates {


    private Double latitude;
    private Double longitude;
    public Coordinates() { }

    private Coordinates(final Double latitude, final Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
        return longitude;
    }

    public void setLongitude(final Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "latitude=" + latitude +
                ", Longitude=" + longitude +
                '}';
    }
}
