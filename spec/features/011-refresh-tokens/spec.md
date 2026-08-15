# 011 · Refresh tokens
**Estado:** implementado

## Qué hace

El login y el registro devuelven, además del access token, un refresh token de larga duración. Con él se obtienen nuevos access tokens sin reingresar credenciales (`POST /api/v1/auth/refresh`) y se puede cerrar sesión revocándolo (`POST /api/v1/auth/logout`). Los refresh tokens se almacenan solo con hash en base de datos, se rotan en cada uso y son revocables.

## Por qué

El access token actual es un JWT stateless que dura 24 h y no puede invalidarse antes de expirar. Si se compromete, la ventana de exposición es larga; y el usuario debe volver a loguearse cada 24 h. Con refresh tokens se reduce la vida del access token y se habilita revocación (logout, rotación y detección de reuso).

## Criterios de aceptación

- [ ] `POST /api/v1/auth/login` y `POST /api/v1/auth/register` devuelven `accessToken`, `refreshToken` y `expiraEn` (epoch ms del access token)
- [ ] El refresh token nunca se persiste en claro: solo su hash SHA-256, con relación al usuario y fecha de expiración
- [ ] `POST /api/v1/auth/refresh` con un refresh válido devuelve un par nuevo (access + refresh) y revoca el refresh usado (rotación)
- [ ] Reutilizar un refresh ya revocado responde 401 y revoca todos los refresh activos del usuario (detección de robo)
- [ ] Refresh token expirado responde 401
- [ ] Refresh token de un usuario inexistente o inactivo responde 401
- [ ] `POST /api/v1/auth/logout` revoca el refresh presentado y responde 204
- [ ] El access token sigue siendo JWT stateless; los endpoints protegidos no cambian
- [ ] `jwt.access-expiration` (corta, p. ej. 15 min) y `jwt.refresh-expiration` (p. ej. 30 días) configurables en `application.yaml`
- [ ] Los endpoints `refresh` y `logout` son públicos (no exigen access token)
- [ ] Los errores mantienen el formato `{error, message, status, timestamp}`

## Fuera de alcance

- Persistir o revocar el access token (sigue stateless)
- Sesiones múltiples por dispositivo (las rotaciones revocan tokens, no sesiones)
- Refresh tokens para clientes OAuth2
- Limpieza programada de tokens expirados (posible `@Scheduled` futuro)
