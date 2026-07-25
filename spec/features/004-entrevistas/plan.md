# 004 · Entrevistas — Plan

## Enfoque

CRUD anidado. Las entrevistas se crean y listan bajo una candidatura (`/candidaturas/{id}/entrevistas`). La actualización y eliminación se hacen por ID de entrevista (`/entrevistas/{id}`).

## Implementación

1. Crear enum `TipoEntrevista`
2. Crear entity `Entrevista` con `@Entity` + `@Table(name = "entrevistas")`
3. Crear `EntrevistaRepository` con `findByCandidaturaId`
4. Crear DTOs: `EntrevistaRequest`, `EntrevistaResponse`
5. Crear `EntrevistaService`: CRUD + validación de pertenencia
6. Crear `EntrevistaController`: endpoints REST anidados
7. Tests unitarios

## Decisiones

- Las entrevistas se crean bajo `/candidaturas/{id}/entrevistas` para mantener RESTfulness
- La actualización y eliminación usan `/entrevistas/{id}` (más simple)
- No se valida que la candidatura esté en un estado específico para crear entrevistas

## Riesgos

- **Entrevistas huérfanas** — Si se elimina una candidatura, las entrevistas se eliminan en cascada (`ON DELETE CASCADE`)
