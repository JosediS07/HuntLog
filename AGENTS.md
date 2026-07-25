# Reglas para el agente

## Convención de commits

Usar formato conventional commits: `tipo(scope): mensaje`

Tipos: feat, fix, refactor, test, docs, chore
Scope: auth, empresa, candidatura, entrevista, estadistica, busqueda, shared

Ejemplos:
- feat(auth): agregar login con JWT
- fix(candidatura): corregir transición de estados inválida
- refactor(empresa): extraer lógica de validación

## Commits atómicos

Cada commit debe agrupar archivos de un solo cambio lógico y atómico.
NO mezclar feat + fix + refactor en el mismo commit.
Usar `git add -p` si un mismo archivo toca varios temas separados.

## Pull Requests

Al crear una PR, incluir una descripción breve en lenguaje cotidiano explicando qué cambió y por qué. Ejemplo:

```
Se agregó el módulo de autenticación con JWT.
Ahora los usuarios pueden registrarse, iniciar sesión
y acceder a endpoints protegidos.
```

## Clean Code

### General
- Nombres de clases, métodos, variables y parámetros en español, descriptivos y pronunciables. No abreviaturas (ej. `obtenerUsuario` no `getUsr`).
- Una sola responsabilidad por clase, método y componente (SRP).
- Métodos de ≤ 20 líneas (excluyendo firma y llaves). Si necesita más, extraer sub-métodos.
- Early return: validar precondiciones al inicio y salir rápido, evitar if-else anidados.
- Sin comentarios: el código debe ser auto-documentado con nombres claros.

### Backend (Java)
- DTOs como `record` inmutable para request/response.
- `@Transactional` explícito en servicios, nunca en controllers.
- Inyección de dependencias por constructor (no `@Autowired` en campos).

## Setup Local

El backend usa `application.yaml` con variables de entorno del sistema (inyectadas por IntelliJ, Render, etc.).

### Backend

1. Configurar variables de entorno:
```
DB_URL=jdbc:postgresql://localhost:5432/huntlog
DB_USER=postgres
DB_PASSWORD=postgres
JWT_SECRET=<base64-de-256-bits>
JWT_EXPIRATION=86400000
ADZUNA_APP_ID=<tu-app-id>
ADZUNA_APP_KEY=<tu-app-key>
```

2. Ejecutar: `./mvnw spring-boot:run`

### Base de datos

- PostgreSQL con esquema en `src/main/resources/schema.sql`
- `ddl-auto: validate` — Hibernate solo valida contra el schema existente
- `defer-datasource-initialization: true` — schema.sql corre antes que Hibernate valide
