# HuntLog — Roadmap

## Completado

_(Ninguno aún)_

## En progreso

_(Ninguno aún)_

## Pendiente

1. **001 Auth** — Registro, login JWT, perfil de usuario
2. **002 Empresas** — CRUD completo de empresas
3. **003 Candidaturas** — CRUD + máquina de estados + historial
4. **004 Entrevistas** — CRUD anidado a candidaturas
5. **005 Estadísticas** — Métricas: total por estado, tasa respuesta, tiempo medio
6. **006 Búsqueda** — Consumo de Adzuna API para buscar ofertas reales

## Backlog

- Dashboard admin con métricas globales
- Filtros avanzados en candidaturas (por empresa, rango de fechas, salario)
- Exportar candidaturas a CSV/PDF
- Notificaciones por email al cambiar estado
- Refresh tokens
- Paginación en todos los listados

## Regla

Cada nueva feature se crea como `features/NNN-nombre-feature/` con `spec.md`, `plan.md` y `tasks.md` ANTES de tocar código.
