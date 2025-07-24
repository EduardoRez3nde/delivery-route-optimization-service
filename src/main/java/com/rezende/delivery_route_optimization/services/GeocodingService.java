package com.rezende.delivery_route_optimization.services;

import com.rezende.delivery_route_optimization.dto.AddressRequestDTO;
import com.rezende.delivery_route_optimization.dto.LocationIQResponseDTO;
import com.rezende.delivery_route_optimization.factories.Factory;
import com.rezende.delivery_route_optimization.services.exceptions.GeocodingException;
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

@Service
public class GeocodingService {

    private String apiKey;
    private String baseUrl;
    private final WebClient webClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(GeocodingService.class);

    public GeocodingService(
            @Value("${geocoding.locationIQ.api.key}") final String apiKey,
            @Value("${geocoding.locationIQ.api.baseurl}") final String baseUrl,
            final WebClient.Builder webClient
    ) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.webClient = webClient.baseUrl(baseUrl).build();
    }

    @Cacheable(
            value = "geocoding",
            key = "T(com.rezende.delivery_route_optimization.factories.Factory).toFormattedString(#addressDTO)"
    )
    public Mono<LocationIQResponseDTO> geoCodeAddress(final AddressRequestDTO addressDTO) {

        final String addressFormatted = Factory.toFormattedString(addressDTO);
        LOGGER.info("Iniciando geocodificação para endereço: '{}'", addressFormatted);

        return this.webClient.get()
                .uri(uriBuilder -> {
                    String uri = uriBuilder
                            .queryParam("key", this.apiKey)
                            .queryParam("q", addressFormatted)
                            .queryParam("format", "json")
                            .build()
                            .toString();
                    LOGGER.debug("Chamando LocationIQ GET URL: {}", uri);
                    return uriBuilder
                            .queryParam("key", this.apiKey)
                            .queryParam("q", addressFormatted)
                            .queryParam("format", "json")
                            .build();
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> {
                            LOGGER.error("LocationIQ retornou erro 4xx para '{}': Status {}", addressFormatted, clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new GeocodingException("LocationIQ: Erro 4xx - " + clientResponse.statusCode() + " - " + body)));
                        })
                .onStatus(status -> status.is5xxServerError(),
                        clientResponse -> {
                            LOGGER.error("LocationIQ retornou erro 5xx para '{}': Status {}", addressFormatted, clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(body -> Mono.error(new GeocodingException("LocationIQ: Erro 5xx - " + clientResponse.statusCode() + " - " + body)));
                        })
                .onStatus(status -> status.value() == 429,
                        clientResponse -> {
                            LOGGER.warn("LocationIQ retornou 429 Too Many Requests para: '{}'", addressFormatted);
                            return Mono.error(new TooManyRequestsException("LocationIQ: Limite de taxa atingido para " + addressFormatted));
                        })
                .bodyToMono(LocationIQResponseDTO[].class)
                .flatMap(responses -> {
                    if (responses != null && responses.length > 0) {
                        LocationIQResponseDTO firstResult = responses[0];
                        if (firstResult.getLat() != null && firstResult.getLon() != null) {
                            LOGGER.info("Geocodificação bem-sucedida para '{}': Lat={}, Lon={}", addressFormatted, firstResult.getLat(), firstResult.getLon());
                            return Mono.just(firstResult);
                        } else {
                            LOGGER.warn("LocationIQ retornou resposta vazia ou coordenadas nulas para '{}'", addressFormatted);
                            return Mono.error(new GeocodingException("Coordenadas nulas no resultado LocationIQ para: " + addressFormatted));
                        }
                    } else {
                        LOGGER.warn("LocationIQ não encontrou resultados para o endereço: '{}'", addressFormatted);
                        return Mono.error(new GeocodingException("Endereço não encontrado por LocationIQ: " + addressFormatted));
                    }
                })
                .delayElement(Duration.ofMillis(1000))
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(5))
                        .filter(e -> e instanceof TooManyRequestsException || e instanceof IOException || e instanceof GeocodingException)
                        .doBeforeRetry(retrySignal ->
                                LOGGER.warn("Tentando novamente geocodificação (erro de API/rede/dados) para '{}': Tentativa {}/{}",
                                        addressFormatted, retrySignal.totalRetries() + 1, 5)
                        )
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            LOGGER.error("Todas as tentativas de geocodificação para '{}' falharam.", addressFormatted);
                            return new GeocodingException("Geocodificação esgotou todas as retentativas para: " + addressFormatted, retrySignal.failure());
                        })
                )
                .onErrorResume(e -> {
                    LOGGER.error("Falha FINAL na geocodificação para '{}': {}", addressFormatted, e.getMessage(), e);
                    if (e instanceof GeocodingException) {
                        return Mono.error(e);
                    }
                    return Mono.error(new GeocodingException("Erro inesperado na geocodificação para: " + addressFormatted, e));
                });
    }
}