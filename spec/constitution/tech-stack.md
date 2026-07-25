# HuntLog — Tech Stack

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Backend | Java 25, Spring Boot 4.1.0, Spring Security, Spring Data JPA |
| Base de datos | PostgreSQL 16 |
| Autenticación | JWT stateless (`Authorization: Bearer <token>`) |
| API externa | Adzuna API (WebClient) |
| Validación | Bean Validation (@NotNull, @Email, etc.) |
| Build | Maven |
| Tests | JUnit 5 + Mockito (unit), `@DataJpaTest` (integración) |
| Infraestructura | Docker multi-stage, GitHub Actions CI/CD |

## Arquitectura

**Modular Monolith** — Cada dominio es un módulo independiente con sus controllers, services, repositories, DTOs y exceptions.

```
com.huntlog/
├── auth/           ← Autenticación, JWT, usuarios
├── empresa/        ← CRUD de empresas
├── candidatura/    ← CRUD de candidaturas + máquina de estados
├── entrevista/     ← CRUD de entrevistas
├── estadistica/    ← Métricas y dashboards
├── busqueda/       ← Consumo de Adzuna API
└── shared/         ← Config transversal, excepciones, DTOs comunes
```

## Modelo de datos

| Tabla | Descripción |
|-------|-------------|
| `users` | Usuarios del sistema (email único, password hasheada, rol) |
| `companies` | Empresas donde se aplicó |
| `candidaturas` | Postulaciones con estado, salario, ubicación |
| `entrevistas` | Entrevistas asociadas a una candidatura |
| `historial_estado` | Trail de cambios de estado de cada candidatura |

## Enums

| Enum | Valores |
|------|---------|
| `EstadoCandidatura` | DRAFT, APPLIED, PHONE_SCREEN, TECHNICAL_INTERVIEW, FINAL_INTERVIEW, OFFER, HIRED, REJECTED, WITHDRAWN |
| `TipoEntrevista` | PHONE, VIDEO, ONSITE, TECHNICAL, HR, MANAGER |

## Invariante crítico

Las transiciones de estado deben seguir la máquina de estados definida. No se puede saltar de REJECTED a OFFER, ni de HIRED a cualquier otro estado.

## Límites duros

- No devolver passwords en respuestas de la API.
- No guardar `.env` ni secretos en el repositorio.
- No agregar dependencias nuevas sin actualizar este archivo.
- Preferir JPQL sobre SQL nativo.
- No permitir modificación de datos de otros usuarios desde endpoints normales.
- `@Transactional` siempre en services, nunca en controllers.
