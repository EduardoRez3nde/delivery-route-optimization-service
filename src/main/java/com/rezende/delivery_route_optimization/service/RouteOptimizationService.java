package com.rezende.delivery_route_optimization.service;

import com.rezende.delivery_route_optimization.dto.RouteOptimizedRequestDTO;
import com.rezende.delivery_route_optimization.dto.RouteOptimizedResponseDTO;
import com.rezende.delivery_route_optimization.entities.Address;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RouteOptimizationService {

    private final GeocodingService geocodingService;
    private final OrsMatrixService orsMatrixService;


    public RouteOptimizationService(GeocodingService geocodingService, OrsMatrixService orsMatrixService) {
        this.geocodingService = geocodingService;
        this.orsMatrixService = orsMatrixService;
    }

    public Mono<RouteOptimizedResponseDTO> optimizeDeliveryRoute(final RouteOptimizedRequestDTO route) {

        final List<Address> allAddresses = new ArrayList<>();
        return null;
    }

    private Map<Address, Map<Address, Double>> buildGraphFromOrsResponse(final List<Address> addresses, final List<List<Double>> matrix) {

        final Map<Address, Map<Address, Double>> graph = new HashMap<>();

        for (int i = 0; i < addresses.size(); i++) {

            final Address origin = addresses.get(i);
            graph.putIfAbsent(origin, new HashMap<>());

            for (int j = 0; j < addresses.size(); j++) {

                if (i == j) continue;

                final Address destination = addresses.get(j);
                final Double value = matrix.get(i).get(j);

                graph.get(origin).put(destination, value);
            }
        }
        return graph;
    }


}
