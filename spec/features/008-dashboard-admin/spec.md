# 008 · Dashboard admin
**Estado:** propuesta

## Qué hace

Expone un panel de métricas globales de toda la aplicación, accesible solo para el rol `ADMIN`. El admin ve: total de usuarios, total de empresas, total de candidaturas, candidaturas por estado y tasa de respuesta global.

## Por qué

El operador de la aplicación necesita conocer el uso general del sistema (cuántos usuarios, empresas y candidaturas existen y su distribución) sin revisar cada registro. No existe hoy ningún endpoint con visión global: todas las métricas están limitadas al usuario autenticado.

## Criterios de aceptación

- [ ] `GET /api/admin/dashboard` devuelve: `totalUsuarios`, `totalEmpresas`, `totalCandidaturas`, `candidaturasPorEstado`, `tasaRespuesta` (globales)
- [ ] Solo accesible con token de usuario con rol `ADMIN`
- [ ] Un usuario con rol `USER` recibe `403` al intentar acceder
- [ ] Un request sin token recibe `401` (comportamiento existente)
- [ ] El error `403` se devuelve en el formato JSON estándar `ErrorResponse`
- [ ] Al arrancar, si `ADMIN_EMAIL` y `ADMIN_PASSWORD` están definidos y el email no existe, se crea el usuario admin con rol `ADMIN`
- [ ] Si se define solo una de las dos variables, la app falla al arrancar con mensaje claro
- [ ] Si no se definen, la app arranca normalmente y el seed se omite

## Fuera de alcance

- Listados de usuarios/empresas/candidaturas para revisión
- CRUD de usuarios por parte del admin
- Modificar roles de usuarios
- Dashboard por rango de fechas
