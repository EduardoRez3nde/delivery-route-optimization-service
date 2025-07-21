package com.rezende.delivery_route_optimization.dto;

import com.rezende.delivery_route_optimization.entities.Address;
import com.rezende.delivery_route_optimization.entities.Coordinates;

public class CoordinatesDTO {

    private Double latitude;
    private Double longitude;

    public CoordinatesDTO() { }

    public CoordinatesDTO(final Double latitude, final Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Coordinates toEntity(final CoordinatesDTO dto) {
        return Coordinates.from(dto.getLatitude(), dto.getLongitude());
    }

    public static CoordinatesDTO of(final Address address) {
        return new CoordinatesDTO(address.getCoordinates().getLatitude(), address.getCoordinates().getLongitude());
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
