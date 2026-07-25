# 001 · Auth
**Estado:** pendiente

## Qué hace

Permite a los usuarios registrarse e iniciar sesión con email y password. Devuelve un JWT que se usa para acceder a los endpoints protegidos.

## Por qué

Sin autenticación no hay forma de asociar candidaturas y empresas a un usuario específico. Es la base de todo el sistema.

## Criterios de aceptación

- [ ] `POST /api/v1/auth/register` crea un usuario con nombre, email y password
- [ ] El email debe ser único — si ya existe, devuelve 400
- [ ] La password se almacena hasheada con BCrypt
- [ ] `POST /api/v1/auth/login` devuelve un JWT válido
- [ ] Si las credenciales son incorrectas, devuelve 401
- [ ] `GET /api/v1/auth/me` devuelve el perfil del usuario autenticado
- [ ] Los endpoints protegidos rechazan peticiones sin token o con token inválido
- [ ] Los errores tienen formato consistente: `{error, message, status, timestamp}`

## Fuera de alcance

- Refresh tokens
- Confirmación de email
- Recuperación de password
- OAuth2 / login con redes sociales
