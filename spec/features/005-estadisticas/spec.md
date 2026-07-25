# 005 · Estadísticas
**Estado:** pendiente

## Qué hace

Endpoint que devuelve métricas del usuario: total de candidaturas, distribución por estado, tasa de respuesta y tiempo medio hasta la primera respuesta.

## Por qué

El usuario necesita ver un resumen rápido de su proceso de búsqueda para identificar cuellos de botella.

## Criterios de aceptación

- [ ] `GET /api/v1/stats` devuelve las estadísticas del usuario autenticado
- [ ] Incluye: `totalCandidaturas`, `porEstado` (mapa), `tasaRespuesta`, `tiempoMedioRespuestaDias`
- [ ] `tasaRespuesta` = candidaturas con estado distinto de DRAFT / total de candidaturas
- [ ] `tiempoMedioRespuestaDias` = promedio de días entre `aplicadoEn` y `respondidoEn`
- [ ] Si no hay candidaturas, devuelve ceros

## Fuera de alcance

- Estadísticas por rango de fechas
- Gráficas y dashboards visuales
- Exportar estadísticas
- Estadísticas globales (todos los usuarios)
