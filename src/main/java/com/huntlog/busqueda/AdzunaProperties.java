package com.huntlog.busqueda;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adzuna")
public record AdzunaProperties(
        String baseUrl,
        String appId,
        String appKey
) {}
