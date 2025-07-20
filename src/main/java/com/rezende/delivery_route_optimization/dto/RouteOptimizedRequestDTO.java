package com.rezende.delivery_route_optimization.dto;

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

    public AddressDTO getOrigin() {
        return origin;
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
