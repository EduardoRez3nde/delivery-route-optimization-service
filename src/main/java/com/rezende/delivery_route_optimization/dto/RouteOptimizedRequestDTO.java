package com.rezende.delivery_route_optimization.dto;

import com.rezende.delivery_route_optimization.entities.Address;
import com.rezende.delivery_route_optimization.mapper.AddressMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RouteOptimizedRequestDTO {

    private AddressRequestDTO origin;
    private final List<AddressRequestDTO> destinations = new ArrayList<>();
    private String profile;

    public RouteOptimizedRequestDTO() { }

    private RouteOptimizedRequestDTO(final AddressRequestDTO origin, final List<AddressRequestDTO> destinations, final String profile) {
        this.origin = origin;
        this.destinations.addAll(destinations);
    }

    public static RouteOptimizedRequestDTO from(final AddressRequestDTO origin, final List<AddressRequestDTO> destinations, final String profile) {
        return new RouteOptimizedRequestDTO(origin, destinations, profile);
    }

    public AddressRequestDTO getOriginDTO() {
        return origin;
    }

    public List<AddressRequestDTO> getDestinations() {
        return destinations;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Address toOriginEntity(String id) {

        Objects.requireNonNull(origin, "Origin AddressRequestDTO cannot be null.");

        return AddressMapper.toAddressEntity(id, this.origin);
    }

    public void setOrigin(AddressRequestDTO origin) {
        this.origin = origin;
    }
}
