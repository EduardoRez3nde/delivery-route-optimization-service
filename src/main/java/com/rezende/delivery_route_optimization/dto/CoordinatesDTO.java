package com.rezende.delivery_route_optimization.dto;

import com.rezende.delivery_route_optimization.entities.Coordinates;

public class CoordinatesDTO {

    private Double latitude;
    private Double longitude;

    private CoordinatesDTO(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static CoordinatesDTO of(Coordinates entity) {
        return new CoordinatesDTO(entity.getLatitude(), entity.getLongitude());
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