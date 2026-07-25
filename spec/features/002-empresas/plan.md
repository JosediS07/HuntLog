# 002 · Empresas — Plan

## Enfoque

CRUD simple con paginación. Cada empresa está vinculada al `usuario_id` del creador. Los endpoints filtran por usuario autenticado.

## Implementación

1. Crear entity `Empresa` con `@Entity` + `@Table(name = "companies")`
2. Crear `EmpresaRepository` con `findByUsuarioId` paginado
3. Crear DTOs: `EmpresaRequest`, `EmpresaResponse` (records)
4. Crear `EmpresaService`: CRUD con validación de pertenencia
5. Crear `EmpresController`: endpoints REST con paginación
6. Tests unitarios

## Decisiones

- Paginación por defecto: 20 elementos por página
- `DELETE` es físico (no lógico) por ahora
- El `usuarioId` se extrae del JWT via `@RequestAttribute`

## Riesgos

- **Ninguno significativo** — CRUD estándar
