package com.rezende.delivery_route_optimization.services;

import com.rezende.delivery_route_optimization.dto.*;
import com.rezende.delivery_route_optimization.entities.Address;
import com.rezende.delivery_route_optimization.entities.Metric;
import com.rezende.delivery_route_optimization.entities.RouteOptimized;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
        allAddresses.add(Address.of("origin", route.getOrigin()));

        final AtomicInteger id = new AtomicInteger(1);
        route.getDestinations().forEach(address ->
                allAddresses.add(Address.of("client_" + id, AddressDTO.toEntity(address))));

        final List<Mono<Address>> geocodingMono = allAddresses.stream()
                .map(address -> geocodingService.geoCodeAddress(AddressRequestDTO.of(address))
                        .map(coordinates -> {
                            address.setCoordinates(LocationIQResponseDTO.toEntity(coordinates));
                            return address;
                        })
                        .onErrorResume(error -> {
                            System.err.println("Falha ao geocodificar " + address + ": " + error.getMessage());
                            return Mono.empty();
                        })
                ).toList();

        return Mono.zip(geocodingMono, results -> {
            final List<Address> geocodedAddresses = (List<Address>) Arrays.asList(results).stream()
                    .filter(result -> result instanceof Address)
                    .map(result -> (Address) result)
                    .toList();
            if (geocodedAddresses.size() != geocodingMono.size())
                throw new IllegalStateException("Nem todos os endereços puderam ser geocodificados. Verifique os endereços fornecidos.");
            return geocodedAddresses;
        })
        .flatMap(geocodedAddresses -> {
            final List<List<Double>> orsCoordinates = geocodedAddresses.stream()
                    .map(coordinate -> List.of(coordinate.getCoordinates().getLongitude(), coordinate.getCoordinates().getLatitude()))
                    .toList();
            return orsMatrixService.getMatrix(route.getProfile(), orsCoordinates)
                    .map(orsResponse -> {
                        final Map<Address, Map<Address, Double>> distanceGraph = buildGraphFromOrsResponse(geocodedAddresses, orsResponse.getDistances());
                        final Map<Address, Map<Address, Double>> durationGraph = buildGraphFromOrsResponse(geocodedAddresses, orsResponse.getDurations());

                        final RouteOptimized optimizedRoute = applyNearestNeighborTSP(
                                geocodedAddresses.getFirst(),
                                geocodedAddresses.subList(1, geocodedAddresses.size()),
                                distanceGraph,
                                durationGraph
                        );

                        List<AddressDTO> addressDTOS = optimizedRoute.getAddresses().stream().
                                map(AddressDTO::of)
                                .toList();

                        final MetricDTO metric = MetricDTO.from(optimizedRoute.getMetric());

                        return RouteOptimizedResponseDTO.from(addressDTOS, metric);
                    });
        })
        .onErrorResume(e -> {
            System.err.println("Erro na otimização da rota: " + e.getMessage());
            return Mono.error(new RuntimeException("Não foi possível otimizar a rota. Detalhes: " + e.getMessage(), e));
        });
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
