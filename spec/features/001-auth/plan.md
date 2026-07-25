# 001 · Auth — Plan

## Enfoque

Módulo de autenticación con JWT stateless. El usuario se registra, obtiene un token, y lo envía en cada petición como `Authorization: Bearer <token>`.

## Implementación

1. Agregar dependencias: `spring-boot-starter-security`, `jjwt-api/impl/jackson`
2. Crear entity `User` con `@Entity` + `@Table(name = "users")`
3. Crear `UserRepository` con `findByEmail` y `existsByEmail`
4. Crear DTOs: `LoginRequest`, `RegisterRequest`, `AuthResponse` (records)
5. Crear `JwtService`: generar token, extraer claims, validar expiración
6. Crear `JwtFilter`: interceptor que extrae token del header `Authorization`
7. Crear `AuthService`: registrar (con hash BCrypt) y login (con AuthenticationManager)
8. Crear `AuthController`: endpoints `/register`, `/login`, `/me`
9. Crear `SecurityConfig`: cadena de filtros, rutas públicas vs protegidas
10. Crear `GlobalExceptionHandler`: errores consistentes
11. Tests unitarios para `AuthService` y `JwtService`

## Decisiones

- JWT con HMAC-SHA256 (simple, suficiente para este proyecto)
- Sin refresh tokens por ahora (backlog)
- `User` es una entity JPA dentro del módulo `auth` (no shared)
- `SecurityConfig` en `shared/config` para que sea accesible desde otros módulos

## Riesgos

- **Token expirado sin refresh** — El usuario debe hacer login de nuevo. Aceptable para MVP.
- **Secret hardcodeado** — Se usa variable de entorno `JWT_SECRET`. Nunca en código.
