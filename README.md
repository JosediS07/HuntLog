# HuntLog

Sistema de gestión de candidaturas laborales con autenticación JWT, máquina de estados, entrevistas, estadísticas y búsqueda de ofertas reales vía Adzuna API.

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Backend | Java 25, Spring Boot 4.1.0, Spring Security, Spring Data JPA |
| Base de datos | PostgreSQL 16 |
| Autenticación | JWT stateless (`Authorization: Bearer <token>`) |
| API externa | Adzuna API (WebClient) |
| Documentación API | OpenAPI 3.0 (Swagger UI) |
| Build | Maven |
| Infraestructura | Docker multi-stage, GitHub Actions CI/CD |

## Requisitos

- Java 25+, PostgreSQL 16+

## Ejecutar

### Docker (recomendado)

```bash
docker compose up --build
```

### Desarrollo local

```bash
./mvnw spring-boot:run
```

### Variables de entorno

> La aplicación no arranca sin `JWT_SECRET`: el servicio JWT falla al iniciar con un mensaje claro si no está definida.

| Variable | Descripción | Obligatoria | Default |
|----------|------------|:-----------:|---------|
| `DB_URL` | URL de conexión a PostgreSQL | Sí | — |
| `DB_USER` | Usuario de PostgreSQL | Sí | — |
| `DB_PASSWORD` | Contraseña de PostgreSQL | Sí | — |
| `JWT_SECRET` | Clave secreta para JWT (base64, 256 bits mínimo) | Sí | — |
| `JWT_EXPIRATION` | Expiración del access token en ms | No | `900000` (15 min) |
| `JWT_REFRESH_EXPIRATION` | Expiración del refresh token en ms | No | `2592000000` (30 días) |
| `ADMIN_EMAIL` | Email del usuario admin (seed automático al arrancar) | No* | — |
| `ADMIN_PASSWORD` | Contraseña del usuario admin | No* | — |
| `ADZUNA_APP_ID` | App ID de Adzuna API | Sí** | — |
| `ADZUNA_APP_KEY` | App Key de Adzuna API | Sí** | — |
| `ADZUNA_TIMEOUT_SECONDS` | Timeout de la llamada a Adzuna | No | `10` |

> *Solo si se quiere el usuario admin: `ADMIN_EMAIL` y `ADMIN_PASSWORD` deben definirse juntos.
> **Solo obligatorio para el módulo de búsqueda.

## Modelo de datos

```
┌──────────────┐       ┌──────────────────┐       ┌───────────────────┐
│    users     │       │    companies     │       │   candidaturas    │
├──────────────┤       ├──────────────────┤       ├───────────────────┤
│ id (PK)      │       │ id (PK)          │◄──┐   │ id (PK)           │
│ nombre       │       │ nombre           │   │   │ empresa_id (FK)   │──┘
│ email (uniq) │       │ sitio_web        │   │   │ usuario_id (FK)   │──┐
│ password     │       │ industria        │   │   │ puesto            │  │
│ rol          │       │ ubicacion        │   │   │ estado (enum)     │  │
│ activo       │       │ logo_url         │   │   │ url_oferta        │  │
│ creado       │       │ usuario_id (FK)  │───┘   │ salario_min/max   │  │
└──────────────┘       │ creado           │       │ moneda            │  │
                       │ actualizado      │       │ ubicacion         │  │
                       └──────────────────┘       │ notas             │  │
                                                  │ aplicado_en       │  │
                                                  │ respondido_en     │  │
                                                  │ creado            │  │
                                                  │ actualizado       │  │
                                                  └───────────────────┘  │
                                                                        │
┌───────────────────┐       ┌──────────────────┐                        │
│    entrevistas    │       │ historial_estado │                        │
├───────────────────┤       ├──────────────────┤                        │
│ id (PK)           │       │ id (PK)          │                        │
│ candidatura_id(FK)│──┐    │ candidatura_id   │◄───────────────────────┘
│ tipo (enum)       │  │    │ estado_anterior  │
│ fecha_hora        │  │    │ estado_nuevo     │
│ duracion_min      │  │    │ cambiado_en      │
│ entrevistador     │  │    └──────────────────┘
│ feedback          │  │
│ notas             │  │
│ creado            │  │
└───────────────────┘  │
                       │
                       └──┘
```

### Enums

| Enum | Valores |
|------|---------|
| `EstadoCandidatura` | DRAFT, APPLIED, PHONE_SCREEN, TECHNICAL_INTERVIEW, FINAL_INTERVIEW, OFFER, HIRED, REJECTED, WITHDRAWN |
| `TipoEntrevista` | PHONE, VIDEO, ONSITE, TECHNICAL, HR, MANAGER |

### Máquina de estados

```
DRAFT → APPLIED → PHONE_SCREEN → TECHNICAL_INTERVIEW → FINAL_INTERVIEW → OFFER → HIRED
         │              │                  │                   │
         └──→ REJECTED ←┴────→ REJECTED ←──┴────→ REJECTED ←──┘
         │
         └──→ WITHDRAWN
```

## API

Todas las rutas requieren autenticación excepto `register`, `login`, `refresh` y `logout`. El access token se envía como `Authorization: Bearer <accessToken>`.

### Autenticación (`/api/v1/auth`)

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| POST | `/register` | Registrar usuario | Público |
| POST | `/login` | Iniciar sesión | Público |
| POST | `/refresh` | Rotar refresh token y obtener un par nuevo | Público* |
| POST | `/logout` | Revocar el refresh token | Público* |
| GET | `/me` | Obtener perfil | Autenticado |

> *`refresh` y `logout` son públicos porque usan el refresh token como credencial; así se permite cerrar sesión aunque el access token haya expirado. La respuesta de `register`/`login`/`refresh` incluye `accessToken`, `refreshToken` y `expiraEn`. Cada uso de `refresh` revoca el token anterior; reutilizar uno ya revocado invalida todos los refresh del usuario.

### Empresas (`/api/empresas`)

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| GET | `/` | Listar empresas (paginado) | Autenticado |
| GET | `/{id}` | Obtener empresa | Autenticado |
| POST | `/` | Crear empresa | Autenticado |
| PUT | `/{id}` | Actualizar empresa | Autenticado |
| DELETE | `/{id}` | Eliminar empresa | Autenticado |

### Candidaturas (`/api/candidaturas`)

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| GET | `/` | Listar candidaturas (paginado, filtros) | Autenticado |
| GET | `/exportar?formato=csv` | Exportar candidaturas a CSV/PDF | Autenticado |
| GET | `/{id}` | Obtener candidatura | Autenticado |
| POST | `/` | Crear candidatura | Autenticado |
| PUT | `/{id}` | Actualizar candidatura | Autenticado |
| PATCH | `/{id}/estado` | Cambiar estado (validado) | Autenticado |
| DELETE | `/{id}` | Eliminar candidatura | Autenticado |

Filtros (listado y exportación): `estado`, `empresaId`, `fechaDesde`, `fechaHasta`, `salarioDesde`, `salarioHasta`

### Entrevistas (`/api/candidaturas/{id}/entrevistas`)

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| GET | `/` | Listar entrevistas de candidatura | Autenticado |
| POST | `/` | Crear entrevista | Autenticado |
| PUT | `/api/entrevistas/{id}` | Actualizar entrevista | Autenticado |
| DELETE | `/api/entrevistas/{id}` | Eliminar entrevista | Autenticado |

### Estadísticas (`/api/stats`)

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| GET | `/` | Métricas del usuario | Autenticado |

### Búsqueda (`/api/ofertas`)

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| GET | `/buscar?q=developer&pais=gb` | Buscar ofertas en Adzuna | Autenticado |

### Admin (`/api/admin`)

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| GET | `/dashboard` | Métricas globales (usuarios, empresas, candidaturas, tasa respuesta) | ADMIN |

## Arquitectura

**Modular Monolith** — Cada dominio es un módulo independiente:

```
com.huntlog/
├── auth/           ← JWT, login, registro
├── admin/          ← Dashboard admin (métricas globales)
├── empresa/        ← CRUD empresas
├── candidatura/    ← CRUD candidaturas + máquina de estados
├── entrevista/     ← CRUD entrevistas
├── estadistica/    ← Métricas
├── busqueda/       ← Adaptador Adzuna API
└── shared/         ← Config, excepciones, DTOs comunes
```

## Pruebas

```bash
# Todos los tests (unitarios)
./mvnw test
```

## Despliegue

El `Dockerfile` multi-stage construye el backend en un solo contenedor:

1. **Etapa 1 (JDK):** Build del JAR
2. **Etapa 2 (JRE):** Runtime ligero con solo el JAR

## Seguridad

- Contraseñas hasheadas con BCrypt
- JWT firmado con HMAC-SHA256; `JWT_SECRET` obligatorio con fail-fast al arrancar
- Access token stateless de vida corta + refresh token persistido con hash SHA-256 y rotación con detección de reuso (revoca todos los tokens del usuario si se reutiliza uno revocado)
- Tokens con expiración configurable
- Sin credenciales en el repositorio (ni en CI)
- Códigos de error HTTP correctos: `401` credenciales/token inválido, `403` cuenta inactiva o sin permisos de admin, `404` recurso inexistente o de otro usuario, `409` email duplicado, `422` regla de negocio, `502` fallo/timeout del servicio externo
- Anti-IDOR: cada usuario solo ve y modifica sus propias empresas, candidaturas y entrevistas
- Rate limiting en `/login`: máx. 10 intentos por minuto por IP (configurable con `huntlog.rate-limit.max-intentos` y `huntlog.rate-limit.ventana-segundos`)
