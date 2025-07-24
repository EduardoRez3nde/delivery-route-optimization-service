package com.rezende.delivery_route_optimization.dto;

import com.rezende.delivery_route_optimization.entities.Address;
import com.rezende.delivery_route_optimization.mapper.AddressMapper;

public class AddressRequestDTO {

    private String id;
    private String street;
    private String number;
    private String neighborhood;
    private String city;
    private CoordinatesDTO coordinates;

    public AddressRequestDTO(
            final String id,
            final String street,
            final String number,
            final String neighborhood,
            final String city,final CoordinatesDTO coordinates
    ) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.neighborhood = neighborhood;
        this.city = city;
        this.coordinates = coordinates;
    }

    public static Address toEntity(AddressRequestDTO dto) {
        return AddressMapper.toAddressEntity(dto.getId(), dto);
    }

    public static AddressRequestDTO of(Address entity) {

        return new AddressRequestDTO(
                entity.getId(),
                entity.getStreet(),
                entity.getNumber(),
                entity.getNeighborhood(),
                entity.getCity(),
                AddressMapper.toCoordinatesDTO(entity.getCoordinates())
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public CoordinatesDTO getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinatesDTO coordinates) {
        this.coordinates = coordinates;
    }
}