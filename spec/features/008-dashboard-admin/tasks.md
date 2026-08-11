# 008 · Dashboard admin — Tareas

- [x] Documentar spec, plan y tareas en `spec/features/008-dashboard-admin/`
- [ ] Crear `AdminDashboardResponse` con las métricas globales
- [ ] Agregar query global `contarPorEstadoGlobal` en `CandidaturaRepository`
- [ ] Crear `AdminDashboardService` con el agregado de métricas
- [ ] Crear `AdminController` con `GET /api/admin/dashboard`
- [ ] Proteger `/api/admin/**` con `hasRole("ADMIN")` en `SecurityConfig`
- [ ] Crear `RestAccessDeniedHandler` que devuelva 403 en formato `ErrorResponse`
- [ ] Agregar seed de admin (`AdminSeedRunner`) con fail-fast si falta una variable
- [ ] Configurar `admin.email`/`admin.password` en `application.yaml`
- [ ] Tests unitarios del service (agregado de métricas)
- [ ] Tests de integración: admin ve dashboard, USER recibe 403
- [ ] Actualizar README (endpoint, variables de entorno)
- [ ] Actualizar `.env.example` con `ADMIN_EMAIL`/`ADMIN_PASSWORD`
- [ ] Verificar que `./mvnw test` pasa completo
- [ ] Mover feature a "Done" en roadmap.md
