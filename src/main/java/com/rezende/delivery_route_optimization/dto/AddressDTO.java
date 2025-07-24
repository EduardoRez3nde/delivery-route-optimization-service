package com.rezende.delivery_route_optimization.dto;

import com.rezende.delivery_route_optimization.entities.Address;

public class AddressDTO {

    private String id;
    private String street;
    private String number;
    private String neighborhood;
    private String city;

    private CoordinatesDTO coordinates;

    public AddressDTO() { }

    private AddressDTO(
            final String id,
            final String street,
            final String number,
            final String neighborhood,
            final String city,
            final CoordinatesDTO coordinates
    ) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.neighborhood = neighborhood;
        this.city = city;
        this.coordinates = coordinates;
    }

    public static AddressDTO of(
            final String id,
            final String street,
            final String number,
            final String neighborhood,
            final String city,
            final CoordinatesDTO coordinatesDTO
    ) {
        return new AddressDTO(
                id,
                street,
                number,
                neighborhood,
                city,
                coordinatesDTO
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
