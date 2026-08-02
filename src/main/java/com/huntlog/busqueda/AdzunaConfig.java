package com.huntlog.busqueda;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(AdzunaProperties.class)
public class AdzunaConfig {

    private final AdzunaProperties adzunaProperties;

    public AdzunaConfig(AdzunaProperties adzunaProperties) {
        this.adzunaProperties = adzunaProperties;
    }

    @Bean
    public WebClient adzunaWebClient() {
        return WebClient.builder()
                .baseUrl(adzunaProperties.baseUrl())
                .build();
    }
}
