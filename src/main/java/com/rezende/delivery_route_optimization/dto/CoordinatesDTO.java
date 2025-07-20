package com.rezende.delivery_route_optimization.dto;

public class CoordinatesDTO {

    private Double latitude;
    private Double longitude;

    public CoordinatesDTO() { }

    public CoordinatesDTO(final Double latitude, final Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
