package com.rezende.delivery_route_optimization.mapper;

import com.rezende.delivery_route_optimization.dto.LocationIQResponseDTO;
import com.rezende.delivery_route_optimization.entities.Coordinates;

public final class GeocodingMapper {

    private GeocodingMapper() {}

    public static Coordinates toCoordinatesEntity(LocationIQResponseDTO dto) {
        if (dto == null || dto.getLat() == null || dto.getLon() == null) {
            return null;
        }
        try {
            return Coordinates.from(Double.parseDouble(dto.getLat()), Double.parseDouble(dto.getLon()));
        } catch (NumberFormatException e) {
            System.err.println("Erro de formato de número ao converter lat/lon: " + dto.getLat() + ", " + dto.getLon());
            return null;
        }
    }
}
