# 003 · Candidaturas
**Estado:** pendiente

## Qué hace

CRUD de candidaturas con máquina de estados. Cada candidatura tiene una empresa, un puesto, estado, salario, ubicación y notas. El estado cambia mediante un endpoint dedicado que valida las transiciones permitidas.

## Por qué

Es el corazón del sistema. Sin candidaturas no hay tracker. La máquina de estados asegura que las transiciones sean lógicas y auditables.

## Criterios de aceptación

- [ ] `POST /api/v1/candidaturas` crea una candidatura con empresaId, puesto y estado inicial (DRAFT)
- [ ] `GET /api/v1/candidaturas` lista las candidaturas del usuario (paginado, con filtros)
- [ ] `GET /api/v1/candidaturas/{id}` obtiene una candidatura por ID
- [ ] `PUT /api/v1/candidaturas/{id}` actualiza datos de la candidatura
- [ ] `PATCH /api/v1/candidaturas/{id}/estado` cambia el estado validando la transición
- [ ] `DELETE /api/v1/candidaturas/{id}` elimina una candidatura
- [ ] La máquina de estados permite: DRAFT→APPLIED→PHONE_SCREEN→TECHNICAL_INTERVIEW→FINAL_INTERVIEW→OFFER→HIRED
- [ ] Desde cualquier estado activo se puede ir a REJECTED o WITHDRAWN
- [ ] HIRED, REJECTED y WITHDRAWN son estados finales (no permiten más transiciones)
- [ ] Cada cambio de estado se registra en `historial_estado`
- [ ] Filtros: estado, empresaId, fechaDesde, fechaHasta
- [ ] `aplicadoEn` se registra automáticamente al pasar de DRAFT a APPLIED
- [ ] `respondidoEn` se registra automáticamente al pasar de APPLIED a cualquier estado de entrevista

## Fuera de alcance

- Candidaturas con múltiples postulaciones al mismo puesto
- Notificaciones por email al cambiar estado
- Archivos adjuntos (CV, cover letter)
