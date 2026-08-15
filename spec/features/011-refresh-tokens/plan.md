# 011 · Refresh tokens — Plan

## Enfoque

Dos tipos de token: access JWT stateless de vida corta y refresh token opaco (valor aleatorio de 256 bits) guardado con hash SHA-256 en la tabla `refresh_tokens`. El refresh se rota en cada uso con una actualización atómica de compare-and-set para detectar reuso.

Flujo:

```
login/register  → genera access + refresh, persiste hash
refresh         → valida token, revoca el presentado (CAS), crea uno nuevo, devuelve el par nuevo
logout          → revoca el presentado y responde 204
```

## Implementación

1. Config en `application.yaml`:
   - `jwt.access-expiration: ${JWT_EXPIRATION:900000}` (15 min)
   - `jwt.refresh-expiration: ${JWT_REFRESH_EXPIRATION:2592000000}` (30 días)
   - Actualizar `.env.example` y `README` con `JWT_REFRESH_EXPIRATION`
2. Entidad `RefreshToken` (id, usuario FK con `ON DELETE CASCADE`, `tokenHash` único, `expiraEn`, `revocadoEn` nullable, `creadoEn`) y su `RefreshTokenRepository`
3. `RefreshTokenService`:
   - `generar(User)` → valor aleatorio `SecureRandom` (32 bytes), hash SHA-256, persistir
   - `rotar(String token)` → buscar por hash; validar existencia, expiración y estado del usuario; revocar con CAS (`UPDATE ... SET revocado_en = NOW() WHERE id = ? AND revocado_en IS NULL`); si 0 filas → reuso → revocar todos los del usuario → 401; si OK, generar el nuevo par
   - `revocar(String token)` → CAS sobre el presentado
4. `JwtService`: `generarToken(User, long expiracionMs)`; mantener el resto intacto
5. DTOs: `RefreshRequest { refreshToken }`; `AuthResponse` pasa a `{id, nombre, email, rol, accessToken, refreshToken, expiraEn}`; nuevo `UsuarioResponse {id, nombre, email, rol}` para `/me`
6. `AuthController`: `POST /refresh`, `POST /logout`; ajustar `login`, `register` y `me`
7. `SecurityConfig`: agregar `/api/v1/auth/refresh` y `/api/v1/auth/logout` a las rutas públicas
8. Excepción `RefreshTokenInvalidoException` (401) mapeada en `GlobalExceptionHandler`
9. Tests unitarios (`RefreshTokenService`, `JwtService`) y de integración (flujo login → refresh → logout)

## Decisiones

- **Refresh opaco en DB, no JWT**: permite revocación y detección de reuso; un JWT de larga duración no puede invalidarse antes de expirar. Costo: una tabla y una query por refresh.
- **Hash SHA-256 en vez de texto plano**: si la base se filtra, los refresh no sirven para suplantar sesiones.
- **Rotación obligatoria con CAS**: cada refresh invalida el token anterior; reutilizar uno revocado indica robo y se revoca todo el usuario (patrón OWASP).
- **Endpoints refresh/logout públicos**: el refresh token es la credencial; no exigir access permite hacer logout con el access vencido.
- **Conservar la variable `JWT_EXPIRATION`** para el access: no rompe deploys existentes; solo se agrega `JWT_REFRESH_EXPIRATION`. El default baja de 24 h a 15 min.
- **`/me` deja de exponer tokens** con un DTO dedicado (`UsuarioResponse`): nunca debió devolver el access y menos el refresh. El campo `token` se renombra a `accessToken`.
- **Comparar expiración en epoch ms** (`Instant`): evita desfases de zona horaria entre servidor y base.

## Riesgos

- **Carrera de reuso (dos refresh simultáneos)**: mitigado con la actualización atómica `revocarSiActivo`; solo una transacción afecta la fila.
- **Tokens huérfanos por usuarios eliminados**: FK con `ON DELETE CASCADE`.
- **Acumulación de tokens expirados**: fuera de alcance; se mitiga en el futuro con limpieza `@Scheduled`.
- **Cambio de contrato en login/register**: rompe clientes que lean `token`; se documenta en README.
- **Sobrecarga de tablas en `schema.sql`**: la tabla nueva se agrega con `CREATE TABLE IF NOT EXISTS`, consistente con el resto del esquema.
