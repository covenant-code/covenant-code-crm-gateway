package com.covenantcode.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    // Overridable via env var CRM_SERVICE_URL.
    // Default "crm-service" resolves via Docker Swarm DNS within the same stack (no underscore issue with Java 21 URI).
    @Value("${crm.service.url:http://crm-service:8080}")
    private String crmServiceUrl;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // All CRM business API
                .route("crm-api", r -> r
                        .path("/api/**")
                        .filters(f -> f.dedupeResponseHeader(
                                "Access-Control-Allow-Credentials Access-Control-Allow-Origin", "RETAIN_FIRST"))
                        .uri(crmServiceUrl))
                // OpenAPI docs proxied through gateway (useful for dev/staging)
                .route("crm-swagger-ui", r -> r
                        .path("/swagger-ui/**", "/swagger-ui.html", "/webjars/**")
                        .uri(crmServiceUrl))
                .route("crm-api-docs", r -> r
                        .path("/api-docs/**")
                        .uri(crmServiceUrl))
                .build();
    }
}
