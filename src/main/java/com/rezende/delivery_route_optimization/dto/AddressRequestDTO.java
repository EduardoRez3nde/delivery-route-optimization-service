package com.rezende.delivery_route_optimization.dto;

import com.rezende.delivery_route_optimization.entities.Address;

public class AddressRequestDTO {

    private String street;
    private String number;
    private String neighborhood;
    private String city;

    public AddressRequestDTO() { }

    public AddressRequestDTO(String street, String number, String neighborhood, String city) {
        this.street = street;
        this.number = number;
        this.neighborhood = neighborhood;
        this.city = city;
    }

    public static AddressRequestDTO from(
            final String street,
            final String number,
            final String neighborhood,
            final String city
    ) {
        return new AddressRequestDTO(street, number, neighborhood, city);
    }

    public static AddressRequestDTO of(final Address address) {
        return AddressRequestDTO.from(address.getStreet(), address.getNumber(), address.getNeighborhood(), address.getCity());
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
}
