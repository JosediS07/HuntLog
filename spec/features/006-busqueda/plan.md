# 006 · Búsqueda Adzuna — Plan

## Enfoque

Adaptador que consume la API de Adzuna vía WebClient (Spring WebFlux). La respuesta de Adzuna se transforma a un DTO propio (`OfertaExternaResponse`).

## Implementación

1. Crear interface `BusquedaPort` (puerto de salida)
2. Crear `AdzunaAdapter` que implementa `BusquedaPort` usando WebClient
3. Crear DTOs: `AdzunaResponse` (respuesta cruda de Adzuna), `OfertaExternaResponse` (DTO propio)
4. Crear `BusquedaService` que usa el puerto
5. Crear `BusquedaController`: endpoint `GET /ofertas/buscar`
6. Configurar `app-id` y `app-key` de Adzuna en `application.yaml`
7. Tests unitarios para el adapter (con MockWebServer o similar)

## Decisiones

- WebClient en vez de RestTemplate (non-blocking, más moderno)
- El `BusquedaPort` permite cambiar de API en el futuro sin modificar el controller
- Los datos de Adzuna se transforman a nuestro DTO (no exponemos la estructura de Adzuna al frontend)
- Sin cache por ahora (el rate limit de Adzuna es generoso: 250/día)

## Riesgos

- **Rate limit de Adzuna** — 250 req/día. Mitigación: cachear resultados por query (futuro).
- **API de Adzuna cambia** — El adaptador aísla el cambio. Solo se modifica `AdzunaAdapter`.
