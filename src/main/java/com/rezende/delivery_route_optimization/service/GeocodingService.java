package com.rezende.delivery_route_optimization.service;

import com.rezende.delivery_route_optimization.dto.AddressRequestDTO;
import com.rezende.delivery_route_optimization.dto.LocationIQResponseDTO;
import com.rezende.delivery_route_optimization.factories.Factory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GeocodingService {

    private final WebClient.Builder webClientBuilder;

    private WebClient webClient;

    @Value("${geocoding.locationIQ.api.key}")
    private String apiKey;

    @Value("${geocoding.locationIQ.api.baseurl}")
    private String baseUrl;

    public GeocodingService(final WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    public void init() {
        this.webClient = this.webClientBuilder.baseUrl(this.baseUrl).build();
    }

    public Mono<LocationIQResponseDTO> geoCodeAddress(final AddressRequestDTO addressDTO) {

        final String address = Factory.toFormattedString(addressDTO);

        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("search.php")
                        .queryParam("key", this.apiKey)
                        .queryParam("q", address)
                        .queryParam("format", "json")
                        .build()
                )
                .retrieve()
                .bodyToMono(LocationIQResponseDTO[].class)
                .flatMap(responses -> {
                    if (responses != null && responses.length > 0)
                        return Mono.just(responses[0]);
                    return Mono.empty();
                });
    }

}
