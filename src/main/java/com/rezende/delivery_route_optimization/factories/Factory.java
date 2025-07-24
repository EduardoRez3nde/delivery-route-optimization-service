package com.rezende.delivery_route_optimization.factories;

import com.rezende.delivery_route_optimization.dto.AddressRequestDTO;
import java.util.Objects;
import java.util.StringJoiner;

public class Factory {

    public static <T extends AddressRequestDTO> String toFormattedString(final T value) {

        StringJoiner joiner = new StringJoiner(", ");

        if (Objects.nonNull(value)) {

            StringJoiner streetAndNumber = new StringJoiner(" ");

            if (value.getStreet() != null && !value.getStreet().trim().isEmpty()) {
                streetAndNumber.add(value.getStreet().trim());
            }
            if (value.getNumber() != null && !value.getNumber().trim().isEmpty()) {
                streetAndNumber.add(value.getNumber().trim());
            }
            if (streetAndNumber.length() > 0) {
                joiner.add(streetAndNumber.toString());
            }

            if (value.getNeighborhood() != null && !value.getNeighborhood().trim().isEmpty()) {
                joiner.add(value.getNeighborhood().trim());
            }

            if (value.getCity() != null && !value.getCity().trim().isEmpty()) {
                joiner.add(value.getCity().trim());
            }

            joiner.add("Amazonas");
            joiner.add("Brasil");
        }

        return joiner.toString();
    }
}