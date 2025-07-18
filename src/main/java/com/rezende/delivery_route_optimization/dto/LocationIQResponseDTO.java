package com.rezende.delivery_route_optimization.dto;

public class LocationIQResponseDTO {

    private Double latitude;
    private Double longitude;

    public LocationIQResponseDTO() { }

    public LocationIQResponseDTO(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
