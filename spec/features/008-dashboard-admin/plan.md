# 008 · Dashboard admin — Plan

## Enfoque

Nuevo módulo `admin` con un endpoint de solo lectura que agrega métricas globales. Las consultas usan los repositorios existentes (contadores por tabla) y una nueva query para agrupar candidaturas por estado sin filtrar por usuario. El acceso se protege a nivel de `SecurityConfig` con `hasRole("ADMIN")` sobre `/api/admin/**`. El seed del admin corre al arranque si las variables `ADMIN_EMAIL`/`ADMIN_PASSWORD` están definidas.

## Implementación

1. Crear módulo `com.huntlog.admin`:
   - `AdminDashboardResponse` (record DTO)
   - `AdminDashboardService` (agrega métricas globales)
   - `AdminController` (`GET /api/admin/dashboard`)
   - `AdminSeedRunner` (`CommandLineRunner` que crea el admin si aplica)
2. Agregar consultas globales:
   - `UserRepository.count()` y `EmpresaRepository.count()` (heredadas)
   - `CandidaturaRepository.count()` (heredada) y `@Query` nueva `contarPorEstadoGlobal()` agrupando por estado sin `usuarioId`
3. Config:
   - `SecurityConfig`: `.requestMatchers("/api/admin/**").hasRole("ADMIN")` antes de `.requestMatchers("/api/**").authenticated()`
   - `RestAccessDeniedHandler` que devuelve `403` en formato `ErrorResponse`
   - `application.yaml`: `admin.email: ${ADMIN_EMAIL:}` y `admin.password: ${ADMIN_PASSWORD:}`; validación fail-fast si solo se define una variable
4. Tests unitarios (`AdminDashboardServiceTest`) y de integración (admin ve dashboard, USER recibe 403)

## Decisiones

- **Protección por ruta en vez de anotaciones `@PreAuthorize`**: se centraliza en `SecurityConfig` y es la misma convención del resto de endpoints.
- **Seed solo si ambas variables existen**: no rompe el arranque en local/CI; si se define una sola, fail-fast con mensaje claro (no debe quedar un admin a medias).
- **Tasa de respuesta global**: mismo cálculo que el módulo de estadísticas pero sobre todas las candidaturas (`total - DRAFT / total`).
- **403 JSON**: se agrega `RestAccessDeniedHandler` para mantener el contrato de `ErrorResponse` que ya usan el resto de errores.

## Riesgos

- **Endpoints admin abiertos por error**: el matcher `hasRole("ADMIN")` se coloca antes del `authenticated()` y se cubre con test de integración (USER → 403).
- **Seed duplicado en tests con contexto compartido**: se usa email fijo único por contexto; el runner ya verifica `existsByEmail`.
- **JWT sin claim de rol**: el token ya incluye `claim("rol", ...)` y `AuthUserDetailsService` genera `ROLE_*`; no requiere cambios en auth.
