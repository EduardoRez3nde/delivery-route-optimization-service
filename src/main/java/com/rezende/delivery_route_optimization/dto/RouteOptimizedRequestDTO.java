package com.rezende.delivery_route_optimization.dto;

import com.rezende.delivery_route_optimization.entities.Address;

import java.util.ArrayList;
import java.util.List;

public class RouteOptimizedRequestDTO {

    private AddressDTO origin;
    private final List<AddressDTO> destinations = new ArrayList<>();
    private String profile;

    public RouteOptimizedRequestDTO() { }

    private RouteOptimizedRequestDTO(final AddressDTO origin, final List<AddressDTO> destinations, final String profile) {
        this.origin = origin;
        this.destinations.addAll(destinations);
    }

    public static RouteOptimizedRequestDTO from(final AddressDTO origin, final List<AddressDTO> destinations, final String profile) {
        return new RouteOptimizedRequestDTO(origin, destinations, profile);
    }

    public AddressDTO getOriginDTO() {
        return origin;
    }

    public Address getOrigin() {
        return Address.from(origin.getId(), origin.getStreet(), origin.getNumber(), origin.getNeighborhood(), origin.getCity(), origin.getCoordinates());
    }

    public void setOrigin(AddressDTO origin) {
        this.origin = origin;
    }

    public List<AddressDTO> getDestinations() {
        return destinations;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
