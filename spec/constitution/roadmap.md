# HuntLog — Roadmap

## Completado

1. **001 Auth** — Registro, login JWT, perfil de usuario
2. **002 Empresas** — CRUD completo de empresas
3. **003 Candidaturas** — CRUD + máquina de estados + historial
4. **004 Entrevistas** — CRUD anidado a candidaturas
5. **005 Estadísticas** — Métricas: total por estado, tasa respuesta, tiempo medio
6. **006 Búsqueda** — Consumo de Adzuna API para buscar ofertas reales
7. **Paginación** — En el listado de candidaturas
8. **Filtros** — Por estado, empresa y rango de fechas en candidaturas
9. **Endurecimiento de seguridad** — 401 real sin token, denyAll, claims JWT validados
10. **Rate limiting en login** — Máximo de intentos por IP configurable
11. **Tests de integración** — Cobertura del flujo completo con H2
12. **Filtro por rango salarial** — Solapamiento en candidaturas (desde/hasta)
13. **Dashboard admin** — Métricas globales con rol ADMIN
14. **Exportar candidaturas** — Descarga a CSV y PDF con filtros
15. **Refresh tokens** — Access token de vida corta + refresh token con hash, rotación y revocación; endpoints `POST /api/v1/auth/refresh` y `/logout`

## En progreso

_(Ninguno aún)_

## Pendiente

_(Ninguno aún)_

## Backlog

- Notificaciones por email al cambiar estado

## Regla

Cada nueva feature se crea como `features/NNN-nombre-feature/` con `spec.md`, `plan.md` y `tasks.md` ANTES de tocar código.
