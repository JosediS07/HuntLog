package com.huntlog.busqueda.dto;

import java.util.List;

public record AdzunaResponse(
        List<AdzunaOferta> results
) {
    public record AdzunaOferta(
            String title,
            AdzunaCompany company,
            String location,
            String redirect_url,
            String description,
            Double salary_min,
            Double salary_max
    ) {}

    public record AdzunaCompany(
            String display_name
    ) {}
}
