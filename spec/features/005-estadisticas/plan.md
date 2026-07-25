# 005 · Estadísticas — Plan

## Enfoque

Queries JPQL en `CandidaturaRepository` que agregan datos. El `EstadisticaService` orquesta las queries y arma el DTO de respuesta.

## Implementación

1. Agregar queries JPQL en `CandidaturaRepository`: contar total, contar por estado, contar con respuesta, tiempo medio
2. Crear DTO `EstadisticaResponse` con los campos calculados
3. Crear `EstadisticaService`: ejecutar queries y calcular métricas
4. Crear `EstadisticaController`: endpoint `GET /stats`
5. Tests unitarios

## Decisiones

- Las queries están en `CandidaturaRepository` (no en un repository separado) porque operan sobre la misma tabla
- `tasaRespuesta` se calcula en el service (no en JPQL) por claridad
- Sin cache por ahora (los datos cambian con cada candidatura)

## Riesgos

- **Performance con muchos registros** — Las queries de agregación pueden ser lentas. Mitigación: índices en las columnas relevantes.
