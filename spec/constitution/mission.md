# HuntLog — Misión

## Qué es

HuntLog es una aplicación web para gestionar candidaturas laborales de forma centralizada. Permite a los usuarios rastrear sus postulaciones, programar entrevistas, consultar estadísticas y buscar ofertas reales en el mercado.

## Para quién

- **Usuarios:** Personas en proceso de búsqueda laboral que quieren organizar sus candidaturas.
- **El autor:** Proyecto de portafolio que demuestra arquitectura modular, autenticación JWT, máquina de estados y consumo de APIs externas.

## Principios

1. **Código limpio y seguro** — Sin credenciales hardcodeadas, contraseñas hasheadas, JWT stateless.
2. **Máquina de estados sólida** — Las transiciones de candidatura deben ser validadas estrictamente.
3. **API-first** — Todo accesible vía REST. El frontend es opcional.
4. **Persistencia eficiente** — Consultas JPQL optimizadas, paginación en todos los listados.
5. **Modular monolith** — Cada módulo es independiente pero comparte la misma base.

## Qué NO es

- No es un ATS empresarial (no tiene equipos, roles de hiring manager, pipelines custom).
- No es un buscador de empleo (no indexa ofertas, solo las muestra desde Adzuna).
- No tiene frontend por ahora — es backend puro.
