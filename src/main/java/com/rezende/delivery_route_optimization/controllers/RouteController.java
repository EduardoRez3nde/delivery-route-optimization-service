package com.rezende.delivery_route_optimization.controllers;

import com.rezende.delivery_route_optimization.dto.RouteOptimizedRequestDTO;
import com.rezende.delivery_route_optimization.dto.RouteOptimizedResponseDTO;
import com.rezende.delivery_route_optimization.services.RouteOptimizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/routes")
public class RouteController {

    private final RouteOptimizationService routeOptimizationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteController.class);

    public RouteController(RouteOptimizationService routeOptimizationService) {
        this.routeOptimizationService = routeOptimizationService;
    }

    @PostMapping("/optimize")
    public Mono<ResponseEntity<RouteOptimizedResponseDTO>> optimizeRoute(@RequestBody final RouteOptimizedRequestDTO routeRequestDTO) {

        LOGGER.info("Requisição de otimização de rota recebida para origem: {}", routeRequestDTO.getOriginDTO().getStreet());
        LOGGER.debug("Detalhes da Requisição: {}", routeRequestDTO);

        return routeOptimizationService.optimizeDeliveryRoute(routeRequestDTO)
                .map(responseDTO -> {
                    LOGGER.info("Rota otimizada retornada com sucesso. Distância total: {}km", responseDTO.getMetric().getDistanceTotalKm());
                    LOGGER.debug("Resposta da Rota Otimizada: {}", responseDTO);
                    return ResponseEntity.ok(responseDTO);
                });
    }
}