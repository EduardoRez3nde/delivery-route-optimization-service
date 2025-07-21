package com.rezende.delivery_route_optimization.service;

import com.rezende.delivery_route_optimization.dto.MetricDTO;
import com.rezende.delivery_route_optimization.dto.RouteOptimizedRequestDTO;
import com.rezende.delivery_route_optimization.dto.RouteOptimizedResponseDTO;
import com.rezende.delivery_route_optimization.entities.Address;
import com.rezende.delivery_route_optimization.entities.Metric;
import com.rezende.delivery_route_optimization.entities.RouteOptimized;
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

    private RouteOptimized applyNearestNeighborTSP(
            final Address origin,
            final List<Address> destinations,
            final Map<Address, Map<Address, Double>> distanceGraph,
            final Map<Address, Map<Address, Double>> durationGraph
    ) {
        Double totalDistance = 0.0;
        Double totalDuration = 0.0;
        final List<Address> currentRoute = new ArrayList<>();

        final List<Address> unvisited = new ArrayList<>(destinations);

        Address current = origin;
        currentRoute.add(current);
        
        while (!unvisited.isEmpty()) {

            Address nextDestination = null;
            double minDistance = Double.MAX_VALUE;

            for (final Address candidate : unvisited) {
                if (distanceGraph.containsKey(current) && distanceGraph.get(current).containsKey(candidate)) {
                    double distance = distanceGraph.get(current).get(candidate);
                    if (distance < minDistance) {
                        minDistance = distance;
                        nextDestination = candidate;
                    }
                }
            }

            if (nextDestination != null) {
                currentRoute.add(nextDestination);
                totalDistance += minDistance;
                totalDuration += durationGraph.get(current).get(nextDestination);
                unvisited.remove(nextDestination);
                current = nextDestination;
            } else {
                System.err.println("Não foi possível encontrar o próximo vizinho para " + current);
                break;
            }
        }
        if (!currentRoute.isEmpty() && !current.equals(origin)) {

            if (distanceGraph.containsKey(current) && distanceGraph.get(current).containsKey(origin)) {
                totalDistance += distanceGraph.get(current).get(origin);
                totalDuration += durationGraph.get(current).get(origin);
                currentRoute.add(origin);
            } else {
                System.err.println("Não foi possível retornar à origem do último ponto.");
            }
        }
        return RouteOptimized.from(currentRoute, Metric.from(totalDistance, totalDuration, "km", "minute"));
    }
}
