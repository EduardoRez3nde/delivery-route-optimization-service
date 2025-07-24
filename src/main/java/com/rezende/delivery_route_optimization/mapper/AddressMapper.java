package com.rezende.delivery_route_optimization.mapper;

import com.rezende.delivery_route_optimization.dto.AddressDTO;
import com.rezende.delivery_route_optimization.dto.AddressRequestDTO;
import com.rezende.delivery_route_optimization.dto.CoordinatesDTO;
import com.rezende.delivery_route_optimization.entities.Address;
import com.rezende.delivery_route_optimization.entities.Coordinates;

import java.util.Objects;

public final class AddressMapper {

    private AddressMapper() {}

    public static Coordinates toCoordinatesEntity(CoordinatesDTO dto) {
        if (dto == null || dto.getLatitude() == null || dto.getLongitude() == null) {
            return null;
        }
        return Coordinates.from(dto.getLatitude(), dto.getLongitude());
    }

    public static CoordinatesDTO toCoordinatesDTO(Coordinates entity) {
        if (entity == null) {
            return null;
        }
        return CoordinatesDTO.of(entity);
    }

    public static Address toAddressEntity(String id, AddressRequestDTO dto) {

        Objects.requireNonNull(dto, "AddressRequestDTO cannot be null for entity conversion.");

        Coordinates coordinates = toCoordinatesEntity(dto.getCoordinates());

        return Address.from(
                id,
                dto.getStreet(),
                dto.getNumber(),
                dto.getNeighborhood(),
                dto.getCity(),
                coordinates
        );
    }

    public static AddressDTO toAddressDTO(Address entity) {
        Objects.requireNonNull(entity, "Address entity cannot be null for DTO conversion.");

        return AddressDTO.of(
                entity.getId(),
                entity.getStreet(),
                entity.getNumber(),
                entity.getNeighborhood(),
                entity.getCity(),
                toCoordinatesDTO(entity.getCoordinates())
        );
    }
}