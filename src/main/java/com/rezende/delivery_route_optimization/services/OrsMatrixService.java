package com.rezende.delivery_route_optimization.services;

import com.rezende.delivery_route_optimization.dto.OrsRequestDTO;
import com.rezende.delivery_route_optimization.dto.OrsResponseDTO;
import com.rezende.delivery_route_optimization.services.exceptions.OrsMatrixException;
import com.rezende.delivery_route_optimization.services.exceptions.TooManyRequestsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Service
public class OrsMatrixService {

    private final String apiKey;

    private final String baseUrl;

    private final WebClient webClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrsMatrixService.class);

    public OrsMatrixService(
            @Value("${geocoding.open-route-service.api.key}") final String apiKey,
            @Value("${geocoding.open-route-service.api.baseurl}") final String baseUrl,
            final WebClient.Builder webClient
    ) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.webClient = webClient.baseUrl(baseUrl).build();
        LOGGER.info("OrsMatrixService inicializado com base URL: {}", this.baseUrl);
    }

    @Cacheable(
            value = "coordinate-matrix",
            key = "#profile + '-' + #coordinates"
    )
    public Mono<OrsResponseDTO> getMatrix(final String profile, List<List<Double>> coordinates) {

        LOGGER.info("Iniciando requisição de matriz ORS para perfil '{}' com {} coordenadas.", profile, coordinates.size());
        LOGGER.debug("Coordenadas enviadas para ORS: {}", coordinates);

        final OrsRequestDTO request = OrsRequestDTO.from(
                coordinates,
                List.of("distance", "duration"),
                "km"
        );
        final String fullUrl = String.format("%s/%s", baseUrl, profile);
        LOGGER.debug("Chamando OpenRouteService POST URL: {}", fullUrl);
        LOGGER.debug("Corpo da requisição ORS: {}", request);

        return webClient.post()
                .uri(fullUrl)
                .header("Authorization", apiKey)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    LOGGER.error("Erro do cliente ORS (4xx) para perfil '{}': Status {}, URL: {}", profile, clientResponse.statusCode(), fullUrl);
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(body -> Mono.error(new OrsMatrixException("Erro 4xx do ORS: " + clientResponse.statusCode() + " - " + body)));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    LOGGER.error("Erro do servidor ORS (5xx) para perfil '{}': Status {}, URL: {}", profile, clientResponse.statusCode(), fullUrl);
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(body -> Mono.error(new OrsMatrixException("Erro 5xx do ORS: " + clientResponse.statusCode() + " - " + body)));
                })
                .onStatus(status -> status.value() == 429, clientResponse -> {
                    LOGGER.warn("OpenRouteService retornou 429 Too Many Requests para perfil '{}'", profile);
                    return Mono.error(new TooManyRequestsException("OpenRouteService: Limite de taxa atingido para perfil " + profile));
                })
                .bodyToMono(OrsResponseDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(e -> e instanceof TooManyRequestsException || e instanceof IOException || e instanceof OrsMatrixException)
                        .doBeforeRetry(retrySignal ->
                                LOGGER.warn("Tentando novamente requisição ORS (erro 429/rede/API) para perfil '{}': Tentativa {}/{}",
                                        profile, retrySignal.totalRetries() + 1, 3)
                        )
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            LOGGER.error("Todas as tentativas para obter matriz ORS para perfil '{}' falharam.", profile);
                            return new OrsMatrixException("Matriz ORS esgotou todas as retentativas para: " + profile, retrySignal.failure());
                        })
                )
                .doOnSuccess(response -> LOGGER.info("Requisição de matriz ORS para perfil '{}' bem-sucedida.", profile))
                .onErrorResume(e -> {
                    LOGGER.error("Falha FINAL ao obter matriz ORS para perfil '{}': {}", profile, e.getMessage(), e);
                    if (e instanceof OrsMatrixException) {
                        return Mono.error(e);
                    }
                    return Mono.error(new OrsMatrixException("Erro inesperado ao obter matriz ORS para perfil " + profile, e));
                });
    }
}