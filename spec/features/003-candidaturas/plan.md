# 003 · Candidaturas — Plan

## Enfoque

Máquina de estados con validación estricta en el service. Cada transición se valida usando el método `puedeTransicionarA()` del enum `EstadoCandidatura`. Los cambios de estado se registran en una tabla de historial.

## Implementación

1. Crear enum `EstadoCandidatura` con lógica de transiciones
2. Crear entity `Candidatura` con `@Entity` + `@Table(name = "candidaturas")`
3. Crear entity `HistorialEstado` con `@Entity` + `@Table(name = "historial_estado")`
4. Crear repositorios con queries de filtrado y paginación
5. Crear DTOs: `CandidaturaRequest`, `CambiarEstadoRequest`, `CandidaturaResponse`
6. Crear `CandidaturaService`: CRUD + `cambiarEstado()` con validación
7. Crear `CandidaturaController` con endpoints REST
8. Tests unitarios para la máquina de estados y el service

## Decisiones

- El enum `EstadoCandidatura` tiene el método `puedeTransicionarA()` — la lógica está en el dominio
- `historial_estado` es una tabla aparte (no un JSON en la candidatura)
- Los timestamps `aplicadoEn` y `respondidoEn` se actualizan automáticamente en el service
- Filtros se implementan con múltiples `findBy` en el repository (no JPA Specification por ahora)

## Riesgos

- **Transiciones concurrentes** — Sin optimistic locking por ahora (no es crítico para un usuario individual)
- **Historial sin rollback** — Si el cambio de estado falla, el historial no se crea (transacción atómica)
