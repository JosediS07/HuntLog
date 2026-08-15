# 011 · Refresh tokens — Tareas

- [x] Documentar spec, plan y tareas en `spec/features/011-refresh-tokens/`
- [ ] Agregar `refresh_tokens` a `schema.sql` (con `ON DELETE CASCADE` a `users`)
- [ ] Configurar `jwt.access-expiration` y `jwt.refresh-expiration` en `application.yaml` (main y test) y `.env.example`
- [ ] Crear entidad `RefreshToken` y su `RefreshTokenRepository`
- [ ] Crear `RefreshTokenService` (generar, rotar con CAS, revocar, detección de reuso)
- [ ] Ajustar `JwtService` para expiración configurable por tipo
- [ ] Crear DTOs `RefreshRequest` y `UsuarioResponse`; actualizar `AuthResponse`
- [ ] Endpoints `POST /api/v1/auth/refresh` y `POST /api/v1/auth/logout` en `AuthController`
- [ ] Exponer `refresh` y `logout` en `SecurityConfig` (rutas públicas)
- [ ] Crear `RefreshTokenInvalidoException` y mapearla (401) en `GlobalExceptionHandler`
- [ ] Tests unitarios de `RefreshTokenService` (rotación, reuso, expiración) y `JwtService`
- [ ] Test de integración del flujo login → refresh → logout
- [ ] Actualizar README (endpoints y variables de entorno)
- [ ] Mover feature a "Done" en roadmap.md
- [ ] Verificar que `./mvnw test` pasa completo
