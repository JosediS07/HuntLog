# 001 · Auth — Tareas

- [ ] Agregar dependencias JWT y Security en pom.xml
- [ ] Crear entity `User` (auth/User.java)
- [ ] Crear `UserRepository`
- [ ] Crear DTOs: `LoginRequest`, `RegisterRequest`, `AuthResponse`
- [ ] Crear `JwtService` (generar, validar, extraer claims)
- [ ] Crear `JwtFilter` (interceptar requests con Bearer token)
- [ ] Crear `AuthService` (registrar + login)
- [ ] Crear `AuthController` (/register, /login, /me)
- [ ] Crear `SecurityConfig` (rutas públicas vs protegidas)
- [ ] Crear `GlobalExceptionHandler` en shared
- [ ] Tests unitarios para `AuthService`
- [ ] Tests unitarios para `JwtService`
- [ ] Verificar que compila sin errores
- [ ] Mover feature a "Done" en roadmap.md
