package com.rezende.delivery_route_optimization.entities;

import com.rezende.delivery_route_optimization.dto.CoordinatesDTO;

import java.util.ArrayList;
import java.util.List;

public class Address {

    private String id;
    private String street;
    private String number;
    private String neighborhood;
    private String city;

    private Coordinates coordinates;

    public Address() { }

    private Address(
            final String id,
            final String street,
            final String number,
            final String neighborhood,
            final String city,
            final Coordinates coordinates
    ) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.neighborhood = neighborhood;
        this.city = city;
        this.coordinates = coordinates;
    }

    public static Address from(
            final String id,
            final String street,
            final String number,
            final String neighborhood,
            final String city,
            final Coordinates coordinates

    ) {
        return new Address(id, street, number, neighborhood, city, coordinates);
    }

    public static Address of(final String id, final Address address) {
        return Address.from(id, address.street, address.number, address.neighborhood, address.city, address.coordinates);
    }

    public String getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", number='" + number + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
