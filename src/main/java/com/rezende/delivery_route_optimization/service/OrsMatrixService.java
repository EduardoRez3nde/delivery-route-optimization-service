package com.rezende.delivery_route_optimization.service;

import com.rezende.delivery_route_optimization.dto.OrsRequestDTO;
import com.rezende.delivery_route_optimization.dto.OrsResponseDTO;
import com.rezende.delivery_route_optimization.entities.Coordinates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OrsMatrixService {

    @Value("${geocoding.open-route-service.api.key}")
    private String apiKey;

    @Value("${geocoding.open-route-service.api.baseurl}")
    private String baseUrl;

    private final WebClient webClient;

    public OrsMatrixService(final WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    public Mono<OrsResponseDTO> getMatrix(final String profile, List<List<Coordinates>> coordinates) {

        final OrsRequestDTO request = OrsRequestDTO.from(
                coordinates,
                List.of("distance", "duration"),
                "km"
        );
        final String fullUrl = String.format("%s/%s", baseUrl, profile);

        return webClient.post()
                .uri(fullUrl)
                .header("Authorization", apiKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(OrsResponseDTO.class)
                .doOnError(e -> System.err.println("Erro ao chamar OpenRouteService para perfil " + profile + ": " + e.getMessage()));
    }


}

