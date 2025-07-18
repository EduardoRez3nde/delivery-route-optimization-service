package com.rezende.delivery_route_optimization.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OrsMatrixService {

    @Value("${geocoding.open-route-service.api.key}")
    private String apiKey;

    @Value("${geocoding.open-route-service.api.baseurl}")
    private String baseUrl;

    private final WebClient webClient;

    public OrsMatrixService(final WebClient webClient) {
        this.webClient = webClient;
    }


}
