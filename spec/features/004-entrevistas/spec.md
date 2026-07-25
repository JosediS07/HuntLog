# 004 · Entrevistas
**Estado:** pendiente

## Qué hace

CRUD de entrevistas asociadas a una candidatura. Cada entrevista tiene tipo (PHONE, VIDEO, ONSITE, TECHNICAL, HR, MANAGER), fecha/hora, duración, entrevistador, feedback y notas.

## Por qué

Los usuarios necesitan registrar sus entrevistas para tener una visión completa del proceso de selección.

## Criterios de aceptación

- [ ] `POST /api/v1/candidaturas/{id}/entrevistas` crea una entrevista asociada a una candidatura
- [ ] `GET /api/v1/candidaturas/{id}/entrevistas` lista las entrevistas de una candidatura
- [ ] `PUT /api/v1/entrevistas/{id}` actualiza una entrevista
- [ ] `DELETE /api/v1/entrevistas/{id}` elimina una entrevista
- [ ] Los campos obligatorios: tipo, fechaHora
- [ ] Si la candidatura no existe, devuelve 404
- [ ] Las entrevistas de una candidatura solo son accesibles por el propietario

## Fuera de alcance

- Recordatorios de entrevista
- Integración con Google Calendar
- Entrevistas recurrentes
