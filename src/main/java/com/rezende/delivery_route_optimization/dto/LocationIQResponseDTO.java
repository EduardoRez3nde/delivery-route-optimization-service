package com.rezende.delivery_route_optimization.dto;

import com.rezende.delivery_route_optimization.entities.Coordinates;

public class LocationIQResponseDTO {

    private Double latitude;
    private Double longitude;

    public LocationIQResponseDTO() { }

    public LocationIQResponseDTO(final Double latitude, final Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static LocationIQResponseDTO from(final Double latitude, final Double longitude) {
        return new LocationIQResponseDTO(latitude, longitude);
    }

    public static Coordinates toEntity(final LocationIQResponseDTO dto) {
        return Coordinates.from(dto.latitude, dto.longitude);
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
