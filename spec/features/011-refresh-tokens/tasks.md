# 011 · Refresh tokens — Tareas

- [x] Documentar spec, plan y tareas en `spec/features/011-refresh-tokens/`
- [x] Agregar `refresh_tokens` a `schema.sql` (con `ON DELETE CASCADE` a `users`)
- [x] Configurar `jwt.access-expiration` y `jwt.refresh-expiration` en `application.yaml` (main y test) y `.env.example`
- [x] Crear entidad `RefreshToken` y su `RefreshTokenRepository`
- [x] Crear `RefreshTokenService` (generar, rotar con CAS, revocar, detección de reuso)
- [x] Ajustar `JwtService` para expiración configurable por tipo
- [x] Crear DTOs `RefreshRequest` y `UsuarioResponse`; actualizar `AuthResponse`
- [x] Endpoints `POST /api/v1/auth/refresh` y `POST /api/v1/auth/logout` en `AuthController`
- [x] Exponer `refresh` y `logout` en `SecurityConfig` (rutas públicas)
- [x] Crear `RefreshTokenInvalidoException` y mapearla (401) en `GlobalExceptionHandler`
- [x] Tests unitarios de `RefreshTokenService` (rotación, reuso, expiración) y `JwtService`
- [x] Test de integración del flujo login → refresh → logout
- [x] Actualizar README (endpoints y variables de entorno)
- [x] Mover feature a "Done" en roadmap.md
- [x] Verificar que `./mvnw test` pasa completo
