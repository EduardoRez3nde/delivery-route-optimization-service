package com.rezende.delivery_route_optimization.factories;

import com.rezende.delivery_route_optimization.dto.AddressRequestDTO;

import java.util.Objects;

public class Factory {

    public static <T extends AddressRequestDTO> String toFormattedString(final T value) {
        StringBuilder sb = new StringBuilder();

        if (Objects.nonNull(value)) {
            if (value.getStreet() != null) sb.append(value.getStreet()).append(" ");
            if (value.getNumber() != null) sb.append(value.getNumber()).append(" ");
            if (value.getNeighborhood() != null) sb.append(value.getNeighborhood()).append(" ");
            if (value.getCity() != null) sb.append(value.getCity());
        }

        return sb.toString();
    }
}
