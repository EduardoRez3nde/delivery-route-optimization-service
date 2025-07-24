package com.rezende.delivery_route_optimization.services;

import com.rezende.delivery_route_optimization.dto.*;
import com.rezende.delivery_route_optimization.entities.Address;
import com.rezende.delivery_route_optimization.entities.Metric;
import com.rezende.delivery_route_optimization.entities.RouteOptimized;
import com.rezende.delivery_route_optimization.factories.Factory;
import com.rezende.delivery_route_optimization.mapper.AddressMapper;
import com.rezende.delivery_route_optimization.mapper.GeocodingMapper;
import com.rezende.delivery_route_optimization.services.exceptions.GeocodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class RouteOptimizationService {

    private final GeocodingService geocodingService;
    private final OrsMatrixService orsMatrixService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteOptimizationService.class);

    public RouteOptimizationService(GeocodingService geocodingService, OrsMatrixService orsMatrixService) {
        this.geocodingService = geocodingService;
        this.orsMatrixService = orsMatrixService;
    }

    public Mono<RouteOptimizedResponseDTO> optimizeDeliveryRoute(final RouteOptimizedRequestDTO routeRequestDTO) {

        LOGGER.info("Iniciando otimização para origem: {}", routeRequestDTO.getOriginDTO().getStreet());
        LOGGER.debug("Detalhes completos da requisição de rota: {}", routeRequestDTO);

        final List<Address> allAddresses = new ArrayList<>();
        allAddresses.add(routeRequestDTO.toOriginEntity("origin"));

        final AtomicInteger idCounter = new AtomicInteger(1);
        routeRequestDTO.getDestinations().forEach(addressDTO ->
                allAddresses.add(AddressMapper.toAddressEntity("client_" + idCounter.getAndIncrement(), addressDTO)));

        LOGGER.debug("Endereços brutos preparados para geocodificação (IDs): {}", allAddresses.stream().map(Address::getId).collect(Collectors.toList()));
        LOGGER.debug("Total de endereços a geocodificar: {}", allAddresses.size());

        LOGGER.info("Iniciando geocodificação SEQUENCIAL para {} endereços (1 requisição/segundo).", allAddresses.size());

        return Flux.fromIterable(allAddresses)
                .concatMap(address -> {
                    LOGGER.debug("Processando geocodificação para endereço ID: {} - Endereço: '{}'", address.getId(), Factory.toFormattedString(AddressRequestDTO.of(address)));
                    return Mono.delay(Duration.ofSeconds(1))
                            .then(geocodingService.geoCodeAddress(AddressRequestDTO.of(address)))
                            .map(locationIQResponse -> {
                                Address mappedAddress = address;
                                mappedAddress.setCoordinates(GeocodingMapper.toCoordinatesEntity(locationIQResponse));
                                LOGGER.debug("Coordenadas geocodificadas para {}: Lat {}, Lon {}", mappedAddress.getId(), mappedAddress.getCoordinates().getLatitude(), mappedAddress.getCoordinates().getLongitude());
                                return mappedAddress;
                            })
                            .onErrorResume(e -> {
                                LOGGER.error("Falha ao geocodificar {} (após retries): {}", address.getId(), e.getMessage());
                                return Mono.error(new GeocodingException("Falha na geocodificação para: " + address.getId() + " - " + Factory.toFormattedString(AddressRequestDTO.of(address))));
                            });
                })
                .collectList()
                .flatMap(geocodedAddresses -> {
                    LOGGER.info("Geocodificação CONCLUÍDA para todos os endereços. Total: {}.", geocodedAddresses.size());
                    LOGGER.debug("Endereços geocodificados: {}", geocodedAddresses.stream().map(a -> a.getId() + " (" + a.getCoordinates().getLatitude() + "," + a.getCoordinates().getLongitude() + ")").collect(Collectors.toList()));

                    if (geocodedAddresses.size() != allAddresses.size()) {
                        LOGGER.error("Falha na validação de geocodificação: {} de {} endereços geocodificados. Isso indica um erro inesperado no fluxo reativo.", geocodedAddresses.size(), allAddresses.size());
                        throw new IllegalStateException("Nem todos os endereços puderam ser geocodificados. Verifique os endereços fornecidos e logs.");
                    }

                    for (Address addr : geocodedAddresses) {
                        if (addr.getCoordinates() == null) {
                            LOGGER.error("Coordenadas nulas encontradas após geocodificação para o endereço: {}", addr.getId());
                            throw new IllegalStateException("Coordenadas ausentes após geocodificação para o endereço: " + addr.getId() + " - " + addr.getStreet() + ". Verifique o serviço de geocodificação.");
                        }
                    }

                    LOGGER.info("Chamando OpenRouteService para calcular matriz de distâncias/durações para {} endereços com perfil '{}'.", geocodedAddresses.size(), routeRequestDTO.getProfile());

                    final List<List<Double>> orsCoordinates = geocodedAddresses.stream()
                            .map(address -> List.of(address.getCoordinates().getLongitude(), address.getCoordinates().getLatitude()))
                            .collect(Collectors.toList());

                    return orsMatrixService.getMatrix(routeRequestDTO.getProfile(), orsCoordinates)
                            .map(orsResponse -> {
                                LOGGER.info("Matriz ORS recebida com sucesso. Processando grafos.");
                                LOGGER.debug("Resposta completa ORS: {}", orsResponse);

                                final Map<Address, Map<Address, Double>> distanceGraph = buildGraphFromOrsResponse(geocodedAddresses, orsResponse.getDistances());
                                final Map<Address, Map<Address, Double>> durationGraph = buildGraphFromOrsResponse(geocodedAddresses, orsResponse.getDurations());

                                LOGGER.debug("Grafos de distância e duração construídos.");
                                LOGGER.debug("Grafo de distância (exemplo de arestas da origem {}): {}",
                                        geocodedAddresses.getFirst().getId(),
                                        distanceGraph.get(geocodedAddresses.getFirst()).entrySet().stream().map(entry -> entry.getKey().getId() + ":" + entry.getValue()).collect(Collectors.joining(", ")));

                                LOGGER.info("Aplicando algoritmo do Vizinho Mais Próximo para otimizar rota.");

                                final RouteOptimized optimizedRoute = applyNearestNeighborTSP(
                                        geocodedAddresses.getFirst(),
                                        geocodedAddresses.subList(1, geocodedAddresses.size()),
                                        distanceGraph,
                                        durationGraph
                                );
                                LOGGER.info("Rota otimizada calculada. Distância total preliminar: {}km", optimizedRoute.getMetric().getDistanceTotalKm());

                                List<AddressDTO> addressDTOS = optimizedRoute.getAddresses().stream()
                                        .map(AddressMapper::toAddressDTO)
                                        .collect(Collectors.toList());

                                final MetricDTO metric = MetricDTO.from(optimizedRoute.getMetric());

                                LOGGER.info("Otimização de rota finalizada com sucesso. Distância final: {}km, Duração final: {} minutos.", optimizedRoute.getMetric().getDistanceTotalKm(), optimizedRoute.getMetric().getTimeTotalMinutes());
                                LOGGER.debug("Rota finalizada (IDs): {}", optimizedRoute.getAddresses().stream().map(Address::getId).collect(Collectors.toList()));

                                return RouteOptimizedResponseDTO.from(addressDTOS, metric);
                            });
                })
                .onErrorResume(e -> {
                    LOGGER.error("Erro fatal na otimização da rota: {}", e.getMessage(), e);
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
