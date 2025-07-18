package com.rezende.delivery_route_optimization.entities;

public class Address {

    private String street;
    private String number;
    private String neighborhood;
    private String city;

    public Address() { }

    private Address(
            final String street,
            final String number,
            final String neighborhood,
            final String city
    ) {
        this.street = street;
        this.number = number;
        this.neighborhood = neighborhood;
        this.city = city;
    }

    public static Address from(
            final String street,
            final String number,
            final String neighborhood,
            final String city
    ) {
        return new Address(street, number, neighborhood, city);
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
