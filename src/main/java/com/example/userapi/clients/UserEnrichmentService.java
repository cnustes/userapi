package com.example.userapi.clients;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class UserEnrichmentService {

    private final RestTemplate restTemplate = new RestTemplate();

    // El nombre "enrichmentService" debe coincidir con el de application.properties
    // "fallbackEnrichment" es el nombre del método de fallback que se ejecutará si el circuito está abierto
    @CircuitBreaker(name = "enrichmentService", fallbackMethod = "fallbackEnrichment")
    public Map<String, Object> getEnrichmentData(String email) {
        log.info("▶️  Llamando al servicio de enriquecimiento externo para el email: {}", email);

        // Simulamos una llamada a un servicio externo que podría fallar.
        // En un caso real, esta URL estaría en application.properties.
        // Para probar, puedes levantar un mock server o simplemente dejar que falle.
        String externalUrl = "http://localhost:9090/api/enrichment/" + email;

        // Esta llamada lanzará una excepción si el servicio en el puerto 9090 no está disponible,
        // lo que contará como un fallo para el Circuit Breaker.
        return restTemplate.getForObject(externalUrl, Map.class);
    }

    /**
     * Este es nuestro método de Fallback.
     * Se ejecuta cuando el Circuit Breaker está abierto.
     * DEBE tener la misma firma que el método original, más un parámetro Throwable al final.
     */
    public Map<String, Object> fallbackEnrichment(String email, Throwable t) {
        log.warn("⚠️  Fallback: El servicio de enriquecimiento no está disponible. Causa: {}. Devolviendo datos por defecto para el email: {}", t.getMessage(), email);
        // Devolvemos un objeto vacío o con datos por defecto.
        // El registro del usuario no se detiene, la aplicación es resiliente.
        return Map.of("status", "UNAVAILABLE", "source", "fallback");
    }
}